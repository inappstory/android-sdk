package com.inappstory.sdk.stories.cache;


import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.utils.ConnectionHeadersMap;
import com.inappstory.sdk.network.utils.ResponseStringFromStream;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.cache.usecases.FinishDownloadFileCallback;
import com.inappstory.sdk.stories.cache.vod.BytesForRange;
import com.inappstory.sdk.stories.cache.vod.ContentRange;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.ByteArrayOutputStream;
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
import java.util.Map;
import java.util.Objects;


/**
 * Загрузчик файлов
 */

public class FilesDownloader {

    private class FileDownloadKey {
        public String url() {
            return url;
        }

        public String filePath() {
            return filePath;
        }

        private final String url;
        private final String filePath;

        private FileDownloadKey(String url, String filePath) {
            this.url = url;
            this.filePath = filePath;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FileDownloadKey)) return false;
            FileDownloadKey that = (FileDownloadKey) o;
            return Objects.equals(url, that.url) &&
                    Objects.equals(filePath, that.filePath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, filePath);
        }
    }

    private class DownloadContextStatus {
        boolean isSuccess;
        String error;
    }

    private class DownloadContext {
        DownloadContextStatus downloadStatus;
        int responseStatus;
        long offset;
        long totalSize;
        String decompression;
        boolean shouldAppend;
    }

    private final IASCore core;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    private final Map<FileDownloadKey, List<FinishDownloadFileCallback>> downloadFileCallbacks = new HashMap<>();

    private final Object finishLock = new Object();

    public FilesDownloader(IASCore core) {
        this(core, 10000, 60000);
    }

    // Конструктор для тестов
    public FilesDownloader(IASCore core, int connectTimeoutMs, int readTimeoutMs) {
        this.core = core;
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
    }

    boolean addFinishCallbackIsNew(String url, String outputFile, FinishDownloadFileCallback callback) {
        boolean isNewUrl = true;
        FileDownloadKey key = new FileDownloadKey(url, outputFile);
        synchronized (finishLock) {
            if (!downloadFileCallbacks.containsKey(key)) {
                downloadFileCallbacks.put(
                        key,
                        new ArrayList<>()
                );
            } else {
                isNewUrl = false;
            }
            if (callback != null)
                downloadFileCallbacks.get(key).add(callback);
            return isNewUrl;
        }
    }

    void invokeFinishCallbacks(String url, String outputFile, DownloadFileState state) {
        List<FinishDownloadFileCallback> callbacks = new ArrayList<>();
        FileDownloadKey key = new FileDownloadKey(url, outputFile);
        synchronized (finishLock) {
            if (downloadFileCallbacks.containsKey(key)) {
                callbacks.addAll(downloadFileCallbacks.remove(key));
            }
        }
        for (FinishDownloadFileCallback callback : callbacks) {
            callback.finish(state);
        }
    }

    public DownloadFileState downloadFile(
            String url,
            File outputFile,
            FileLoadProgressCallback callback,
            ApiLogResponse apiLogResponse,
            DownloadInterruption interruption,
            FinishDownloadFileCallback finishCallback
    ) throws Exception {
        return downloadFile(url,
                outputFile,
                callback,
                apiLogResponse,
                interruption,
                -1,
                finishCallback
        );
    }

    public DownloadFileState downloadFile(
            String url,
            File outputFile,
            FileLoadProgressCallback progressCallback,
            ApiLogResponse apiLogResponse,
            DownloadInterruption interruption,
            long downloadOffset,
            FinishDownloadFileCallback finishCallback
    ) {
        try {
            return downloadFileWithEx(
                    url,
                    outputFile,
                    progressCallback,
                    apiLogResponse,
                    interruption,
                    downloadOffset,
                    finishCallback
            );
        } catch (Exception e) {
            e.printStackTrace();
            logDownloadFileError(url, e.getMessage());
            return null;
        }
    }


    private boolean prepareFileSystem(File outputFile) {
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                InAppStoryManager.showELog(
                        LoggerTags.IAS_ERROR_TAG,
                        "Failed to create directory: " + parentDir.getAbsolutePath()
                );
                return false;
            }
        }
        return true;
    }

    HttpURLConnection createConnection(
            String url,
            long downloadOffset,
            long downloadLimit,
            boolean useEncoding
    ) throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        if (useEncoding)
            urlConnection.setRequestProperty("Accept-Encoding", "br, gzip");
        urlConnection.setConnectTimeout(connectTimeoutMs);
        urlConnection.setReadTimeout(readTimeoutMs);
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", core.network().userAgent());
        if (downloadOffset > 0) {
            if (downloadLimit > downloadOffset) {
                urlConnection.setRequestProperty("Range", "bytes=" + downloadOffset + "-" + downloadLimit);
            } else {
                urlConnection.setRequestProperty("Range", "bytes=" + downloadOffset + "-");
            }
        } else {
            if (downloadLimit > 0) {
                urlConnection.setRequestProperty("Range", "bytes=" + 0 + "-" + downloadLimit);
            }
        }
        urlConnection.connect();
        return urlConnection;
    }

    private DownloadContext getDownloadContext(
            HttpURLConnection urlConnection,
            Map<String, String> headers,
            File outputFile,
            long freeSpace,
            long downloadOffset
    ) throws Exception {
        int status = urlConnection.getResponseCode();
        long totalSize = urlConnection.getContentLength();
        DownloadContext ctx = new DownloadContext();
        ctx.downloadStatus = new DownloadContextStatus();
        ctx.responseStatus = status;
        ctx.downloadStatus.isSuccess = true;
        ctx.offset = downloadOffset;
        if (freeSpace > 0 && totalSize > freeSpace) {
            urlConnection.disconnect();
            ctx.downloadStatus.isSuccess = false;
            ctx.downloadStatus.error = "insufficient disk space";
            return ctx;
        }

        boolean isResuming = ctx.offset > 0;
        boolean serverSupportsRange = (status == 206);

        if (isResuming && !serverSupportsRange) {

            ctx.offset = 0;
            if (outputFile != null)
                outputFile.delete();
        }

        String decompression = null;
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            String key = headerEntry.getKey();
            if (serverSupportsRange && key.equalsIgnoreCase("Content-Range")) {
                String contentRange = headerEntry.getValue();
                if (contentRange != null && contentRange.contains("/")) {
                    try {
                        totalSize = Long.parseLong(
                                contentRange.substring(
                                        contentRange.lastIndexOf('/') + 1
                                )
                        );
                    } catch (Exception e) {

                    }
                }
            }
            if (key.equalsIgnoreCase("Content-Encoding")) {
                decompression = headerEntry.getValue();
            }
        }
        ctx.offset = downloadOffset;
        ctx.totalSize = totalSize;
        ctx.decompression = decompression;
        ctx.shouldAppend = isResuming && serverSupportsRange;
        return ctx;
    }

    private void logDownloadFileError(String url, String error) {
        String errStr = "Failed to download: " + url + "; cause: " + error;
        Log.e("DownloadFileError", errStr);
        InAppStoryManager.showELog(
                LoggerTags.IAS_ERROR_TAG,
                errStr
        );
    }

    private DownloadFileState writeDataToFile(
            HttpURLConnection urlConnection,
            String url,
            File outputFile,
            DownloadContext downloadContext,
            FileLoadProgressCallback progressCallback,
            DownloadInterruption interruption
    ) throws IOException {

        FileOutputStream fileOutputStream = new FileOutputStream(outputFile, downloadContext.shouldAppend);
        FileLock lock = null;
        ResponseStringFromStream responseStringFromStream = new ResponseStringFromStream();
        DownloadFileState state = null;
        try {
            try {
                lock = fileOutputStream.getChannel().tryLock();
            } catch (java.nio.channels.OverlappingFileLockException e) {
                // Для тестов
                lock = null;
            }
            /*  if (lock == null) {
                logDownloadFileError(
                        url,
                        "File " + outputFile.getAbsolutePath() + " is locked by another process"
                );
                return null;
            }*/
            InputStream inputStream = responseStringFromStream.getInputStream(
                    urlConnection.getInputStream(),
                    downloadContext.decompression
            );
            String contentType = urlConnection.getHeaderField("Content-Type");

            if (contentType != null)
                core.keyValueStorage().saveString(outputFile.getName(), contentType);
            else
                core.keyValueStorage().saveString(outputFile.getName(), "image/jpeg");

            byte[] buffer = new byte[8192];
            int bytesRead = 0;
            long downloadedBytes = downloadContext.offset;
            long lastProgressTime = 0;
            final long PROGRESS_THROTTLE_MS = 100;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (interruption != null && interruption.active) {
                    fileOutputStream.getFD().sync();
                    state = new DownloadFileState(outputFile, downloadContext.totalSize, outputFile.length());
                    break;
                }
                fileOutputStream.write(buffer, 0, bytesRead);
                downloadedBytes += bytesRead;
                if (progressCallback != null) {
                    long now = System.currentTimeMillis();
                    if (now - lastProgressTime >= PROGRESS_THROTTLE_MS) {
                        progressCallback.onProgress(downloadedBytes, downloadContext.totalSize);
                        lastProgressTime = now;
                    }
                }
            }
            fileOutputStream.getFD().sync();

            if (state == null) {
                state = new DownloadFileState(outputFile, outputFile.length(), outputFile.length());
            }
        } finally {
            if (lock != null && lock.isValid()) {
                try {
                    lock.release();
                } catch (Exception ignored) {
                }
            }
            try {
                fileOutputStream.close();
            } catch (Exception ignored) {
            }
        }
        return state;
    }

    public BytesForRange downloadBytes(
            String url,
            long from,
            long to,
            long freeSpace
    ) throws Exception {
        HttpURLConnection urlConnection = null;
        //String curl = toCurlRequest(urlConnection, null);
        try {
            urlConnection = createConnection(url, from, to, false);
        } catch (Exception e) {
            logDownloadFileError(url, "Failed to connect");
            return null;
        }

        try {
            int status = urlConnection.getResponseCode();
            if (status >= 400) {
                logDownloadFileError(url, "Failed to connect; Status Code: " + status);
                return null;
            }
            int contentLength = urlConnection.getContentLength();
            if (freeSpace > 0 && contentLength > freeSpace) {
                logDownloadFileError(url, "Insufficient disk space for byte range");
                return null;
            }
            Map<String, String> headers = new ConnectionHeadersMap().get(urlConnection);
            String decompression = null;
            ContentRange contentRange = new ContentRange(from, to, contentLength > 0 ? from + contentLength : to);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                if (key.equalsIgnoreCase("Content-Encoding")) {
                    decompression = entry.getValue();
                } else if (key.equalsIgnoreCase("Content-Range")) {
                    contentRange = StringsUtils.parseContentRangeSafe(entry.getValue(), contentLength);
                }
            }

            return writeBytesToFile(urlConnection, contentRange, decompression, contentLength);
        } catch (Exception e) {
            logDownloadFileError(url, "Failed to download bytes: " + e.getMessage());
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    private BytesForRange writeBytesToFile(
            HttpURLConnection urlConnection,
            ContentRange contentRange,
            String decompression,
            int contentLength
    ) throws IOException {
        ResponseStringFromStream responseStringFromStream = new ResponseStringFromStream();

        InputStream inputStream = responseStringFromStream.getInputStream(
                urlConnection.getInputStream(),
                decompression
        );
        if (contentLength <= 0) {
            contentLength = 8192;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(contentLength);

        byte[] data = new byte[8192];

        int nRead;

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new BytesForRange(contentRange, buffer.toByteArray());
    }

    public DownloadFileState downloadFileWithEx(
            String url,
            File outputFile,
            FileLoadProgressCallback progressCallback,
            ApiLogResponse apiLogResponse,
            DownloadInterruption interruption,
            long downloadOffset,
            FinishDownloadFileCallback finishCallback
    ) throws Exception {
        DownloadFileState state = null;
        if (outputFile == null) return null;

        if (!addFinishCallbackIsNew(
                url,
                outputFile.getAbsolutePath(),
                finishCallback)
        ) {
            if (finishCallback != null)
                finishCallback.waiting();
            return null;
        }

        if (!prepareFileSystem(outputFile)) return null;
        if (downloadOffset > 0 && (!outputFile.exists() || outputFile.length() < downloadOffset)) {
            downloadOffset = 0;
        }

        HttpURLConnection urlConnection = null;
        //String curl = toCurlRequest(urlConnection, null);
        try {
            urlConnection = createConnection(url, downloadOffset, -1, true);
        } catch (Exception e) {
            logDownloadFileError(url, "Failed to connect");
            invokeFinishCallbacks(url, outputFile.getAbsolutePath(), null);
            return null;
        }
        Map<String, String> headers = new ConnectionHeadersMap().get(urlConnection);
        DownloadContext downloadContext = getDownloadContext(
                urlConnection,
                headers,
                outputFile,
                outputFile.getFreeSpace(),
                downloadOffset
        );
        if (!downloadContext.downloadStatus.isSuccess) {
            logDownloadFileError(url, downloadContext.downloadStatus.error);
            invokeFinishCallbacks(url, outputFile.getAbsolutePath(), null);
            return null;
        }
        apiLogResponse.contentLength = downloadContext.totalSize;
        ResponseStringFromStream responseStringFromStream = new ResponseStringFromStream();
        if (downloadContext.responseStatus >= 400) {
            String res = responseStringFromStream.get(
                    urlConnection.getErrorStream(),
                    downloadContext.decompression
            );
            urlConnection.disconnect();
            apiLogResponse.generateFile(downloadContext.responseStatus, res, headers);
            logDownloadFileError(url, res);
            invokeFinishCallbacks(url, outputFile.getAbsolutePath(), null);
            return null;
        }

        try {
            state = writeDataToFile(urlConnection,
                    url,
                    outputFile,
                    downloadContext,
                    progressCallback,
                    interruption
            );
            apiLogResponse.generateFile(downloadContext.responseStatus, outputFile.getAbsolutePath(), headers);
        } catch (Exception e) {
            logDownloadFileError(url, e.getMessage());
            if (outputFile.exists()) {
                state = new DownloadFileState(outputFile, downloadContext.totalSize, outputFile.length());
            }
        } finally {
            urlConnection.disconnect();
            invokeFinishCallbacks(url, outputFile.getAbsolutePath(), state);
        }
        return state;
    }


    public static String toCurlRequest(HttpURLConnection connection, byte[] body) {
        StringBuilder builder = new StringBuilder("curl -v ");

        // Method
        builder.append("-X ").append(connection.getRequestMethod()).append(" \\\n  ");

        // Headers
        for (Map.Entry<String, List<String>> entry : connection.getRequestProperties().entrySet()) {
            builder.append("-H \"").append(entry.getKey()).append(":");
            for (String value : entry.getValue())
                builder.append(" ").append(value);
            builder.append("\" \\\n  ");
        }

        // Body
        if (body != null)
            builder.append("-d '").append(new String(body)).append("' \\\n  ");

        // URL
        builder.append("\"").append(connection.getURL()).append("\"");

        return builder.toString();
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
