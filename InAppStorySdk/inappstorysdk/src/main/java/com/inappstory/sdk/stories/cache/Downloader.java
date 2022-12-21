package com.inappstory.sdk.stories.cache;

import static com.inappstory.sdk.network.NetworkHandler.getResponseFromStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.CacheFontObject;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequestHeader;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
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
    public static String cropUrl(String url) {
        return url;//url.split("\\?")[0];
    }

    public static void downloadFonts(List<CacheFontObject> cachedFonts) {
        if (cachedFonts != null) {
            for (CacheFontObject cacheFontObject : cachedFonts) {
                if (InAppStoryService.isNull()) return;
                downFontFile(cacheFontObject.url, InAppStoryService.getInstance().getCommonCache());
            }
        }
    }


    @NonNull
    @WorkerThread
    public static File downloadOrGetFile(@NonNull String url,
                                         LruDiskCache cache, File img, FileLoadProgressCallback callback) throws Exception {
        return downloadOrGetFile(url, cache, img, callback, null);
    }

    @NonNull
    @WorkerThread
    public static File downloadOrGetFile(@NonNull String url,
                                         LruDiskCache cache, File img, FileLoadProgressCallback callback, String hash) throws Exception {
        String requestId = UUID.randomUUID().toString();
        ApiLogRequest requestLog = new ApiLogRequest();
        ApiLogResponse responseLog = new ApiLogResponse();
        requestLog.method = "GET";
        requestLog.url = url;
        requestLog.isStatic = true;
        requestLog.id = requestId;
        responseLog.id = requestId;
        String key = cropUrl(url);
        HashMap<String, String> headers = new HashMap<>();

        if (cache.hasKey(key)) {
            File file = cache.get(key);
            if (file != null && file.exists()) {
                if (callback != null)
                    callback.onSuccess(file);
                headers.put("From Cache", "true");
                responseLog.generateFile(200, file.getAbsolutePath(), headers);
                InAppStoryManager.sendApiRequestResponseLog(requestLog, responseLog);
                return file;
            }
        }

        InAppStoryManager.sendApiRequestLog(requestLog);
        if (hash != null) {
            ProfilingManager.getInstance().addTask("game_download", hash);
        }
        if (img == null) {
            img = cache.getFileFromKey(key);
        }
        File file = downloadFile(url, img, callback, responseLog);
        if (file != null) {
            cache.put(key, file);
            if (callback != null)
                callback.onSuccess(file);
        } else {
            if (callback != null)
                callback.onError();
        }

        responseLog.responseHeaders.add(new ApiLogRequestHeader("From Cache", "false"));
        InAppStoryManager.sendApiResponseLog(responseLog);
        return file;

    }

    @NonNull
    @WorkerThread
    public static boolean downloadOrGetResourceFile(@NonNull String url, @NonNull String hashKey,
                                                    LruDiskCache cache, File img, FileLoadProgressCallback callback) throws Exception {
        String key = hashKey + "_" + cropUrl(url);
        if (cache.hasKey(key)) {
            File file = cache.get(key);
            if (file != null && file.exists()) {
                return false;
            }
        }
        if (img == null) {
            img = cache.getFileFromKey(key);
        }
        File file = downloadFile(url, img, callback, new ApiLogResponse());
        cache.put(key, file);
        return true;
    }


    public static File getCoverVideo(@NonNull String url,
                                     LruDiskCache cache) {
        String key = cropUrl(url);
        if (cache.hasKey(key)) {
            return cache.get(key);
        } else {
            return null;
        }
    }


    private static final ExecutorService fontDownloader = Executors.newFixedThreadPool(1);
    private static final ExecutorService tmpFileDownloader = Executors.newFixedThreadPool(1);

    private static void downFontFile(final String url, final LruDiskCache cache) {
        fontDownloader.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return downloadOrGetFile(url, cache, null, null);
            }
        });
    }

    public static void downloadFileBackground(final String url, final LruDiskCache cache,
                                              final FileLoadProgressCallback callback) {
        tmpFileDownloader.submit(new Callable() {
            @Override
            public File call() {
                if (cache == null) {
                    if (callback != null)
                        callback.onError();
                    return null;
                }
                try {
                    return downloadOrGetFile(url, cache, null, callback);
                } catch (Exception e) {
                    if (callback != null)
                        callback.onError();
                    return null;
                }
            }
        });
    }


    public static String getFontFile(String url) {
        if (url == null || url.isEmpty()) return null;
        File img = null;
        if (InAppStoryService.isNull()) return null;
        if (InAppStoryService.getInstance().getCommonCache().hasKey(url)) {
            img = InAppStoryService.getInstance().getCommonCache().get(url);
        }
        if (img != null && img.exists()) {
            return img.getAbsolutePath();
        }
        return null;
    }

    private static File downloadFile(String url, File outputFile, FileLoadProgressCallback callback, ApiLogResponse apiLogResponse) throws Exception {

        InAppStoryManager.showDLog("InAppStory_File", url);
        outputFile.getParentFile().mkdirs();
        if (!outputFile.exists())
            outputFile.createNewFile();


        URL urlS = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlS.openConnection();
        urlConnection.setConnectTimeout(300000);
        urlConnection.setReadTimeout(300000);
        urlConnection.setRequestMethod("GET");
        try {
            urlConnection.connect();
        } catch (Exception e) {
            return null;
        }
        int status = urlConnection.getResponseCode();
        HashMap<String, String> headers = new HashMap<>();

        int sz = urlConnection.getContentLength();
        apiLogResponse.contentLength = sz;
        for (String headerKey : urlConnection.getHeaderFields().keySet()) {
            if (headerKey == null) continue;
            if (urlConnection.getHeaderFields().get(headerKey).isEmpty()) continue;
            headers.put(headerKey, urlConnection.getHeaderFields().get(headerKey).get(0));
        }
        if (status > 350) {
            String res = getResponseFromStream(urlConnection.getErrorStream());
            apiLogResponse.generateFile(status, res, headers);
            return null;
        }

        FileOutputStream fileOutput = new FileOutputStream(outputFile);
        FileLock lock = fileOutput.getChannel().lock();
        InputStream inputStream = urlConnection.getInputStream();

        String contentType = urlConnection.getHeaderField("Content-Type");

        if (contentType != null)
            KeyValueStorage.saveString(outputFile.getName(), contentType);
        else
            KeyValueStorage.saveString(outputFile.getName(), "image/jpeg");


        byte[] buffer = new byte[1024];
        int bufferLength = 0;
        int cnt = 0;
        while ((bufferLength = inputStream.read(buffer)) > 0) {
            fileOutput.write(buffer, 0, bufferLength);
            cnt += bufferLength;
            if (callback != null)
                callback.onProgress(cnt, sz);
        }
        fileOutput.flush();
        try {
            lock.release();
        } catch (Exception e) {
        }
        fileOutput.close();
        apiLogResponse.generateFile(status, outputFile.getAbsolutePath(), headers);
        return outputFile;

    }

    public static void compressFile(File srcFile, String mimeType) throws IOException {
        FileOutputStream out = new FileOutputStream(srcFile.getAbsolutePath());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 2;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(srcFile.getPath(), options);
            switch (mimeType) {
                case "image/jpeg":
                case "image/jpg":
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                    break;
                case "image/png":
                    bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
                    break;
                case "image/webp":
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 70, out);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.close();
    }
}
