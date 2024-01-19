package com.inappstory.sdk.stories.cache;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.network.utils.ConnectionHeadersMap;
import com.inappstory.sdk.network.utils.ResponseStringFromStream;
import com.inappstory.sdk.stories.api.models.CacheFontObject;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequestHeader;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Загрузчик файлов
 */

public class Downloader {

    /**
     * Получение ссылки без параметров
     *
     * @param url ссылка
     */
    public static String deleteQueryArgumentsFromUrlOld(String url, boolean delete) {
        return url;//delete ? url.split("\\?")[0] : url;
    }

    public static String deleteQueryArgumentsFromUrl(String url, boolean delete) {
        return delete ? url.split("\\?")[0] : url;
    }

    public static void downloadFonts(final List<CacheFontObject> cachedFonts) {
        if (cachedFonts != null) {
            InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (CacheFontObject cacheFontObject : cachedFonts) {
                        downFontFile(cacheFontObject.url, service.getCommonCache());
                    }
                }
            });
        }
    }


    @WorkerThread
    public static DownloadFileState downloadOrGetFile(
            @NonNull String url,
            boolean cropUrl,
            LruDiskCache cache,
            File img,
            FileLoadProgressCallback callback
    ) throws Exception {
        return downloadOrGetFile(url, cropUrl, cache, img, callback, null, null);
    }

    @WorkerThread
    public static DownloadFileState downloadOrGetFile(
            @NonNull String url,
            boolean deleteArguments,
            LruDiskCache cache,
            File img,
            FileLoadProgressCallback callback,
            DownloadInterruption interruption,
            String hash
    ) throws Exception {
        String requestId = UUID.randomUUID().toString();
        ApiLogRequest requestLog = new ApiLogRequest();
        ApiLogResponse responseLog = new ApiLogResponse();
        requestLog.method = "GET";
        requestLog.url = url;
        requestLog.isStatic = true;
        requestLog.id = requestId;
        responseLog.id = requestId;
        String key = deleteQueryArgumentsFromUrlOld(url, deleteArguments);
        HashMap<String, String> headers = new HashMap<>();
        long offset = 0;
        if (cache.hasKey(key)) {
            DownloadFileState fileState = cache.get(key);
            if (fileState != null
                    && fileState.file != null
                    && fileState.file.exists()
            ) {
                checkAndReplaceFile(cache, url, key, fileState);
                if (fileState.downloadedSize != fileState.totalSize) {
                    offset = fileState.downloadedSize;
                } else {
                    if (callback != null)
                        callback.onSuccess(fileState.file);
                    headers.put("From Cache", "true");
                    responseLog.generateFile(200, fileState.file.getAbsolutePath(), headers);
                    InAppStoryManager.sendApiRequestResponseLog(requestLog, responseLog);
                    return fileState;
                }
            }
        }

        InAppStoryManager.sendApiRequestLog(requestLog);

        if (img == null) {
            img = cache.getFileFromKey(key);
        }
        DownloadFileState fileState = downloadFile(
                url,
                img,
                callback,
                responseLog,
                interruption,
                offset
        );
        if (fileState != null && fileState.file != null) {
            cache.put(key, fileState.file, fileState.totalSize, fileState.downloadedSize);
            if (fileState.totalSize == fileState.downloadedSize) {
                if (callback != null)
                    callback.onSuccess(fileState.file);
            } else {
                if (callback != null)
                    callback.onError("Partial content");
            }
        } else {
            if (callback != null)
                callback.onError("File haven't downloaded");
        }
        responseLog.responseHeaders.add(new ApiLogRequestHeader("From Cache", "false"));
        InAppStoryManager.sendApiResponseLog(responseLog);
        return fileState;

    }

    @NonNull
    @WorkerThread
    public static boolean downloadOrGetResourceFile(
            @NonNull String url,
            @NonNull String key,
            LruDiskCache cache,
            File img,
            FileLoadProgressCallback callback
    ) throws Exception {
        long offset = 0;
        if (cache.hasKey(key)) {
            DownloadFileState fileState = cache.get(key);
            if (fileState != null &&
                    fileState.file != null &&
                    fileState.file.exists()
            ) {
                //checkAndReplaceFile(cache, url, key, fileState);
                if (fileState.totalSize == fileState.downloadedSize)
                    return false;
                else {
                    offset = fileState.downloadedSize;
                }
            }
        }
        if (img == null) {
            img = cache.getFileFromKey(key);
        }

        Log.e("Game_File", "download " + key + " " + url);
        DownloadFileState downloadFileState = downloadFile(
                url,
                img,
                callback,
                new ApiLogResponse(),
                null,
                offset
        );
        if (downloadFileState != null) {
            cache.put(key, downloadFileState.file, downloadFileState.totalSize, downloadFileState.downloadedSize);
        }
        return true;
    }

    public static File updateFile(File file, String url, LruDiskCache cache, String key) throws IOException {
        if (file != null) {
            String extension = getFileExtensionFromUrl(url);
            if (!file.getAbsolutePath().endsWith(extension)) {
                File newFile = new File(file.getAbsolutePath() + extension);
                file.renameTo(newFile);
                cache.put(key, newFile);
                return newFile;
            }
        }
        return file;
    }

    public static File getCoverVideo(@NonNull String url,
                                     LruDiskCache cache) throws IOException {
        String key = deleteQueryArgumentsFromUrlOld(url, false);

        if (cache.hasKey(key)) {
            return updateFile(cache.getFullFile(key), url, cache, key);
        }
        return null;
    }


    private static final ExecutorService fontDownloader = Executors.newFixedThreadPool(1);
    private static final ExecutorService tmpFileDownloader = Executors.newFixedThreadPool(1);

    private static void downFontFile(final String url, final LruDiskCache cache) {
        fontDownloader.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return FileManager.getFullFile(downloadOrGetFile(url, true, cache, null, null));
            }
        });
    }


    public static void downloadFileBackground(
            final String url,
            boolean cropUrl,
            final LruDiskCache cache,
            final FileLoadProgressCallback callback
    ) {
        downloadFileBackground(url, cropUrl, cache, callback, null);
    }

    public static void downloadFileBackground(
            final String url,
            final boolean cropUrl,
            final LruDiskCache cache,
            final FileLoadProgressCallback callback,
            final DownloadInterruption interruption
    ) {
        tmpFileDownloader.submit(new Callable() {
            @Override
            public File call() {
                if (cache == null) {
                    if (callback != null)
                        callback.onError("Cache does not exist");
                    return null;
                }
                try {
                    return FileManager.getFullFile(
                            downloadOrGetFile(
                                    url,
                                    cropUrl,
                                    cache,
                                    null,
                                    callback,
                                    interruption,
                                    null
                            )
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onError(e.getMessage());
                    return null;
                }
            }
        });
    }

    public static String getFileExtensionFromUrl(String url) {
        String croppedUrl = deleteQueryArgumentsFromUrl(url, true);
        String[] parts = croppedUrl.split("/");
        String name = parts[parts.length - 1];
        if (name.contains("."))
            return name.substring(name.lastIndexOf("."));
        else
            return "";
    }

    static void checkAndReplaceFile(
            LruDiskCache cache,
            String url,
            String key,
            DownloadFileState fileState
    ) throws IOException {

        String extension = getFileExtensionFromUrl(url);
        if (!fileState.file.getAbsolutePath().endsWith(extension)) {
            File newFile = new File(fileState.file.getAbsolutePath() + extension);
            fileState.file.renameTo(newFile);
            fileState.file = newFile;
            cache.put(key, newFile);
        }
    }

    public static String getFontFile(String url) {
        if (url == null || url.isEmpty()) return null;
        String key = deleteQueryArgumentsFromUrlOld(url, true);
        File img = null;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        LruDiskCache cache = service.getCommonCache();
        if (cache.hasKey(key)) {
            try {
                img = updateFile(cache.getFullFile(key), url, cache, key);
            } catch (IOException e) {
                img = cache.getFullFile(key);
            }
        }
        if (img != null && img.exists()) {
            return img.getAbsolutePath();
        }
        return null;
    }

    private static DownloadFileState downloadFile(
            String url,
            File outputFile,
            FileLoadProgressCallback callback,
            ApiLogResponse apiLogResponse,
            DownloadInterruption interruption,
            long downloadOffset
    ) throws Exception {

        InAppStoryManager.showDLog("InAppStory_File", url);
        outputFile.getParentFile().mkdirs();
        if (!outputFile.exists())
            outputFile.createNewFile();


        URL urlS = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlS.openConnection();
        urlConnection.setRequestProperty("Accept-Encoding", "br, gzip");
        urlConnection.setConnectTimeout(300000);
        urlConnection.setReadTimeout(300000);
        urlConnection.setRequestMethod("GET");
        if (InAppStoryManager.getNetworkClient() != null) {
            urlConnection.setRequestProperty("User-Agent", InAppStoryManager.getNetworkClient().userAgent);
        }
        if (downloadOffset > 0) {
            urlConnection.setRequestProperty("Range", "bytes=" + downloadOffset + "-");
        }
        try {
            urlConnection.connect();
        } catch (Exception e) {
            return null;
        }
        int status = urlConnection.getResponseCode();
        HashMap<String, String> headers = new HashMap<>();

        long sz = urlConnection.getContentLength();
        long freeSpace = outputFile.getFreeSpace();
        if (freeSpace > 0 && sz > freeSpace) {
            urlConnection.disconnect();
            return null;
        }
        boolean allowPartial = false;

        for (String headerKey : urlConnection.getHeaderFields().keySet()) {
            if (headerKey == null) continue;
            if (urlConnection.getHeaderFields().get(headerKey).isEmpty()) continue;
            headers.put(headerKey, urlConnection.getHeaderFields().get(headerKey).get(0));
            if (headerKey.equalsIgnoreCase("Content-Range")) {
                String rangeHeader = urlConnection.getHeaderFields().get(headerKey).get(0);
                if (!rangeHeader.equalsIgnoreCase("none")) {
                    allowPartial = true;
                    try {
                        sz = Long.parseLong(rangeHeader.split("/")[1]);
                    } catch (Exception e) {

                    }
                }
            }
        }
        apiLogResponse.contentLength = sz;
        String decompression = null;
        HashMap<String, String> responseHeaders = new ConnectionHeadersMap().get(urlConnection);
        if (responseHeaders.containsKey("Content-Encoding")) {
            decompression = responseHeaders.get("Content-Encoding");
        }
        if (responseHeaders.containsKey("content-encoding")) {
            decompression = responseHeaders.get("content-encoding");
        }
        ResponseStringFromStream responseStringFromStream = new ResponseStringFromStream();
        if (status > 350) {
            String res = responseStringFromStream.get(
                    urlConnection.getErrorStream(),
                    decompression
            );
            apiLogResponse.generateFile(status, res, headers);
            return null;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(outputFile,
                allowPartial && downloadOffset > 0);
        FileLock lock = fileOutputStream.getChannel().lock();
        InputStream inputStream = responseStringFromStream.getInputStream(
                urlConnection.getInputStream(),
                decompression
        );

        String contentType = urlConnection.getHeaderField("Content-Type");

        if (contentType != null)
            KeyValueStorage.saveString(outputFile.getName(), contentType);
        else
            KeyValueStorage.saveString(outputFile.getName(), "image/jpeg");
        byte[] buffer = new byte[1024];
        int bufferLength = 0;
        int cnt = 0;
        try {
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                if (interruption != null && interruption.active) {
                    releaseStreamAndFile(fileOutputStream, lock);
                    if (allowPartial)
                        return new DownloadFileState(outputFile, sz, outputFile.length());
                    return null;
                } else {
                    fileOutputStream.write(buffer, 0, bufferLength);
                    cnt += bufferLength;
                    if (callback != null)
                        callback.onProgress(downloadOffset + cnt, sz);
                }
            }
            releaseStreamAndFile(fileOutputStream, lock);
            apiLogResponse.generateFile(status, outputFile.getAbsolutePath(), headers);
            return new DownloadFileState(outputFile, outputFile.length(), outputFile.length());
        } catch (Exception e) {
            releaseStreamAndFile(fileOutputStream, lock);
            if (allowPartial)
                return new DownloadFileState(outputFile, sz, outputFile.length());
            return null;
        }
    }

    private static void releaseStreamAndFile(FileOutputStream fileOutputStream, FileLock lock) throws IOException {
        fileOutputStream.flush();
        try {
            lock.release();
        } catch (Exception e2) {
        }
        fileOutputStream.close();
    }
}
