package com.inappstory.sdk.stories.filedownloader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.utils.lrudiskcache.FileChecker;
import com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.utils.network.utils.ConnectionHeadersMap;
import com.inappstory.sdk.core.utils.network.utils.ResponseStringFromStream;
import com.inappstory.sdk.core.models.logs.ApiLogRequest;
import com.inappstory.sdk.core.models.logs.ApiLogRequestHeader;
import com.inappstory.sdk.core.models.logs.ApiLogResponse;
import com.inappstory.sdk.core.cache.DownloadFileState;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class FileDownload implements IFileDownload {
    protected String url;
    protected List<IFileDownloadCallback> fileDownloadCallbacks = new ArrayList<>();
    private boolean loading = false;
    private final ApiLogRequest requestLog;
    private final ApiLogResponse responseLog;
    private final Long checkSize;
    private final Long needSpace;
    private final String checkSha1;

    protected final LruDiskCache cache;

    private final Object downloadLock = new Object();

    private final String requestId;

    public FileDownload(
            @NonNull String url,
            @NonNull LruDiskCache cache
    ) {
        this.url = url;
        this.cache = cache;
        this.checkSize = null;
        this.checkSha1 = null;
        this.needSpace = null;
        this.requestId = UUID.randomUUID().toString();
        this.requestLog = new ApiLogRequest();
        this.responseLog = new ApiLogResponse();

    }

    public FileDownload(
            @NonNull String url,
            Long checkSize,
            String checkSha1,
            Long needSpace,
            @NonNull LruDiskCache cache
    ) {
        this.url = url;
        this.cache = cache;
        this.checkSize = checkSize;
        this.checkSha1 = checkSha1;
        this.needSpace = needSpace != null ? needSpace : checkSize;
        this.requestId = UUID.randomUUID().toString();
        this.requestLog = new ApiLogRequest();
        this.responseLog = new ApiLogResponse();
    }

    private void generateRequestLog() {
        requestLog.id = requestId;
        requestLog.method = "GET";
        requestLog.url = url;
        requestLog.isStatic = true;
    }

    private void generateResponseLog() {
        responseLog.id = requestId;
    }

    private DownloadFileState downloadFile(long offset) throws Exception {
        URL urlS = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlS.openConnection();
        urlConnection.setRequestProperty("Accept-Encoding", "br, gzip");
        urlConnection.setConnectTimeout(300000);
        urlConnection.setReadTimeout(300000);
        urlConnection.setRequestMethod("GET");
        if (IASCore.getInstance().getNetworkClient() != null) {
            urlConnection.setRequestProperty("User-Agent", IASCore.getInstance().getNetworkClient().userAgent);
        }
        if (offset > 0) {
            urlConnection.setRequestProperty("Range", "bytes=" + offset + "-");
        }
        try {
            urlConnection.connect();
        } catch (Exception e) {
            return null;
        }
        int status = urlConnection.getResponseCode();

        long sz = urlConnection.getContentLength();
        File outputFile = generateFileDirsAndGetOutputFile();
        if (outputFile == null) {
            fileDownloadError(-1, "Error in file name");
            return null;
        }
        long freeSpace = outputFile.getFreeSpace();
        if (freeSpace > 0 && sz > freeSpace) {
            urlConnection.disconnect();
            return null;
        }
        boolean allowPartial = false;

        for (String headerKey : urlConnection.getHeaderFields().keySet()) {
            if (headerKey == null) continue;
            if (urlConnection.getHeaderFields().get(headerKey).isEmpty()) continue;
            if (headerKey.equalsIgnoreCase("Content-Range")) {
                String rangeHeader = urlConnection.getHeaderFields().get(headerKey).get(0);
                if (!rangeHeader.equalsIgnoreCase("none")) {
                    allowPartial = true;
                    try {
                        sz = Long.parseLong(rangeHeader.split("/")[1]);
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        String decompression = null;
        HashMap<String, String> responseHeaders = new ConnectionHeadersMap().get(urlConnection);
        if (responseHeaders.containsKey("Content-Encoding")) {
            decompression = responseHeaders.get("Content-Encoding");
        }
        if (responseHeaders.containsKey("content-encoding")) {
            decompression = responseHeaders.get("content-encoding");
        }
        if (status > 350) {
            String res = new ResponseStringFromStream().get(
                    urlConnection.getErrorStream(),
                    decompression
            );
            fileDownloadError(status, res);
            return null;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(outputFile,
                allowPartial && offset > 0);
        FileLock lock = fileOutputStream.getChannel().lock();
        InputStream inputStream = urlConnection.getInputStream();

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
                if (isInterrupted()) {
                    releaseStreamAndFile(fileOutputStream, lock);
                    if (allowPartial) {
                        return new DownloadFileState(outputFile, sz, outputFile.length());
                    } else {
                        fileDownloadError(-1, "Download interrupted");
                        return null;
                    }
                } else {
                    fileOutputStream.write(buffer, 0, bufferLength);
                    cnt += bufferLength;
                    onProgress(offset + cnt, sz);
                }
            }
            releaseStreamAndFile(fileOutputStream, lock);
            FileChecker fileChecker = new FileChecker();
            if (fileChecker.checkWithShaAndSize(outputFile, checkSize, checkSha1, true)) {
                return new DownloadFileState(outputFile, outputFile.length(), outputFile.length());
            } else {
                fileDownloadError(-1, "File is incorrect");
                return null;
            }
        } catch (Exception e) {
            releaseStreamAndFile(fileOutputStream, lock);
            if (allowPartial) {
                return new DownloadFileState(outputFile, sz, outputFile.length());
            } else {
                fileDownloadError(-1, e.getMessage());
                return null;
            }
        }
    }

    public FileDownload addDownloadCallback(IFileDownloadCallback callback) {
        synchronized (downloadLock) {
            if (callback != null)
                fileDownloadCallbacks.add(callback);
        }
        return this;
    }

    public String getFromCache() throws Exception {
        String key = getCacheKey();
        if (url == null || url.isEmpty() || key == null || key.isEmpty()) {
            fileDownloadError(-1, "Wrong resource key or url");
            return null;
        } else if (cache.hasKey(key)) {
            DownloadFileState fileState = cache.get(key);
            if (fileState != null
                    && fileState.file != null
                    && fileState.file.exists()
            ) {
                addExtensionToOldFile(cache, url, key, fileState);
                if (fileState.downloadedSize == fileState.totalSize) {
                    return fileState.file.getAbsolutePath();
                }
            }
        }
        return null;
    }


    public void downloadOrGetFromCache() throws Exception {
        synchronized (downloadLock) {
            if (loading) return;
            loading = true;
        }
        if (url == null || url.isEmpty()) {
            fileDownloadError(-1, "Wrong resource url");
            return;
        }
        String key = getCacheKey();
        if (key == null || key.isEmpty()) {
            fileDownloadError(-1, "Wrong resource key");
            return;
        }
        generateRequestLog();
        generateResponseLog();
        long offset = 0;
        HashMap<String, String> headers = new HashMap<>();
        if (cache.hasKey(key)) {
            DownloadFileState fileState = cache.get(key);
            if (fileState != null
                    && fileState.file != null
                    && fileState.file.exists()
            ) {
                addExtensionToOldFile(cache, url, key, fileState);
                if (fileState.downloadedSize != fileState.totalSize) {
                    offset = fileState.downloadedSize;
                } else {
                    FileChecker fileChecker = new FileChecker();
                    if (fileChecker.checkWithShaAndSize(
                            fileState.file,
                            checkSize,
                            checkSha1,
                            true
                    )) {
                        fileDownloadSuccess(fileState.file.getAbsolutePath());
                        headers.put("From Cache", "true");
                        responseLog.generateFile(200, fileState.file.getAbsolutePath(), headers);
                        InAppStoryManager.sendApiRequestResponseLog(requestLog, responseLog);
                        return;
                    } else {
                        cache.delete(key);
                    }
                }
            }
        }
        if (needSpace != null && needSpace > cache.getFreeSpace()) {
            fileDownloadError(-1, "No free space to download file");
            return;
        }
        InAppStoryManager.showDLog("InAppStory_File", url);
        InAppStoryManager.sendApiRequestLog(requestLog);

        DownloadFileState fileState = downloadFile(offset);
        if (fileState != null) {
            if (fileState.file != null) {
                cache.put(key, fileState.file, fileState.totalSize, fileState.downloadedSize);
                if (fileState.totalSize == fileState.downloadedSize) {
                    fileDownloadSuccess(fileState.file.getAbsolutePath());
                    responseLog.responseHeaders.add(new ApiLogRequestHeader("From Cache", "false"));
                    InAppStoryManager.sendApiResponseLog(responseLog);
                } else {
                    fileDownloadError(-1, "Partial content");
                }
            } else {
                fileDownloadError(-1, "File haven't downloaded");
            }
        }
    }

    private void fileDownloadSuccess(String path) {
        synchronized (downloadLock) {
            for (IFileDownloadCallback fileDownloadCallback : fileDownloadCallbacks) {
                fileDownloadCallback.onSuccess(path);
            }
            clearCallbacks();
            loading = false;
        }
    }

    protected void clearCallbacks() {
        fileDownloadCallbacks.clear();
    }

    private void fileDownloadError(int errorCode, String error) {
        synchronized (downloadLock) {
            for (IFileDownloadCallback fileDownloadCallback : fileDownloadCallbacks) {
                fileDownloadCallback.onError(errorCode, error);
            }
            clearCallbacks();
            loading = false;
        }
    }

    private void releaseStreamAndFile(FileOutputStream fileOutputStream, FileLock lock) throws IOException {
        fileOutputStream.flush();
        try {
            lock.release();
        } catch (Exception ignored) {
        }
        fileOutputStream.close();
    }

    private File generateFileDirsAndGetOutputFile() throws Exception {
        String path = getDownloadFilePath();
        if (path == null) return null;
        File outputFile = new File(path);
        File parentFile = outputFile.getParentFile();
        if (parentFile != null)
            parentFile.mkdirs();
        if (!outputFile.exists())
            outputFile.createNewFile();
        return outputFile;
    }

    private void addExtensionToOldFile(
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

    private String getFileExtensionFromUrl(String url) {
        String croppedUrl = deleteQueryArgumentsFromUrl(url);
        String[] parts = croppedUrl.split("/");
        String name = parts[parts.length - 1];
        if (name.contains("."))
            return name.substring(name.lastIndexOf("."));
        else
            return "";
    }

    protected String deleteQueryArgumentsFromUrl(String url) {
        return url.split("\\?")[0];
    }


}
