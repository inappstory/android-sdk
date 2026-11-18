package com.inappstory.sdk.stories.cache;

import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.utils.ConnectionHeadersMap;
import com.inappstory.sdk.network.utils.ResponseStringFromStream;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.cache.usecases.FinishDownloadFileCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Загрузчик файлов
 */

public class FilesDownloader {

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

    public FilesDownloader(IASCore core) {
        this.core = core;
    }

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


    public static String getFileExtensionFromUrl(String url) {
        String croppedUrl = deleteQueryArgumentsFromUrl(url, true);
        String[] parts = croppedUrl.split("/");
        String name = parts[parts.length - 1];
        if (name.contains("."))
            return name.substring(name.lastIndexOf("."));
        else
            return "";
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

    private HttpURLConnection createConnection(String url, long downloadOffset) throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestProperty("Accept-Encoding", "br, gzip");
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(60000);
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", core.network().userAgent());
        if (downloadOffset > 0) {
            urlConnection.setRequestProperty("Range", "bytes=" + downloadOffset + "-");
        }
        urlConnection.connect();
        return urlConnection;
    }

    private void invokeFinishCallbacks(
            FilesDownloadManager manager,
            String url,
            String path,
            DownloadFileState state
    ) {
        if (manager != null)
            manager.invokeFinishCallbacks(url, path, state);
    }

    private DownloadContext getDownloadContext(
            HttpURLConnection urlConnection,
            Map<String, String> headers,
            File outputFile,
            long downloadOffset
    ) throws Exception {
        int status = urlConnection.getResponseCode();
        long totalSize = urlConnection.getContentLength();
        long freeSpace = outputFile.getFreeSpace();
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
        InAppStoryManager.showELog(
                LoggerTags.IAS_ERROR_TAG,
                "Failed to download: " + url + "; cause: " + error
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
            lock = fileOutputStream.getChannel().tryLock();
            if (lock == null) {
                logDownloadFileError(
                        url,
                        "File " + outputFile.getAbsolutePath() + " is locked by another process"
                );
                return null;
            }
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
                state = new DownloadFileState(outputFile, downloadContext.totalSize, downloadedBytes);
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
        FilesDownloadManager manager = core.contentLoader().filesDownloadManager();

        if (manager != null && !manager.addFinishCallback(
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
            urlConnection = createConnection(url, downloadOffset);
        } catch (Exception e) {
            logDownloadFileError(url, "Failed to connect");
            invokeFinishCallbacks(manager, url, outputFile.getAbsolutePath(), null);
            return null;
        }
        Map<String, String> headers = new ConnectionHeadersMap().get(urlConnection);
        DownloadContext downloadContext = getDownloadContext(
                urlConnection,
                headers,
                outputFile,
                downloadOffset
        );
        if (!downloadContext.downloadStatus.isSuccess) {
            logDownloadFileError(url, downloadContext.downloadStatus.error);
            invokeFinishCallbacks(manager, url, outputFile.getAbsolutePath(), null);
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
            invokeFinishCallbacks(manager, url, outputFile.getAbsolutePath(), null);
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
            invokeFinishCallbacks(manager, url, outputFile.getAbsolutePath(), state);
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
