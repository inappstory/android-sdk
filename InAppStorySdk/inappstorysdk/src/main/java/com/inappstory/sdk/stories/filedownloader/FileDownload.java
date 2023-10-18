package com.inappstory.sdk.stories.filedownloader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.network.utils.ConnectionHeadersMap;
import com.inappstory.sdk.core.network.utils.ResponseStringFromStream;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequestHeader;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.UUID;

public abstract class FileDownload implements IFileDownload {
    protected String url;
    protected IFileDownloadCallback fileDownloadCallback;
    private final ApiLogRequest requestLog;
    private final ApiLogResponse responseLog;

    protected final LruDiskCache cache;

    private final String requestId;

    public FileDownload(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback,
            @NonNull LruDiskCache cache
    ) {
        this.url = url;
        this.cache = cache;
        this.fileDownloadCallback = fileDownloadCallback;
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
        if (InAppStoryManager.getNetworkClient() != null) {
            urlConnection.setRequestProperty("User-Agent", InAppStoryManager.getNetworkClient().userAgent);
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
            fileDownloadCallback.onError(status, res);
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
                        fileDownloadCallback.onError(-1, "Download interrupted");
                        return null;
                    }
                } else {
                    fileOutputStream.write(buffer, 0, bufferLength);
                    cnt += bufferLength;
                    onProgress(offset + cnt, sz);
                }
            }
            releaseStreamAndFile(fileOutputStream, lock);
            return new DownloadFileState(outputFile, outputFile.length(), outputFile.length());
        } catch (Exception e) {
            releaseStreamAndFile(fileOutputStream, lock);
            if (allowPartial) {
                return new DownloadFileState(outputFile, sz, outputFile.length());
            } else {
                fileDownloadCallback.onError(-1, e.getMessage());
                return null;
            }
        }
    }

    public DownloadFileState downloadOrGetFromCache() throws Exception {
        generateRequestLog();
        generateResponseLog();
        String key = getCacheKey();
        if (key == null) return null;
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
                    fileDownloadCallback.onSuccess(fileState.file.getAbsolutePath());
                    headers.put("From Cache", "true");
                    responseLog.generateFile(200, fileState.file.getAbsolutePath(), headers);
                    InAppStoryManager.sendApiRequestResponseLog(requestLog, responseLog);
                    return fileState;
                }
            }
        }

        InAppStoryManager.showDLog("InAppStory_File", url);
        InAppStoryManager.sendApiRequestLog(requestLog);

        DownloadFileState fileState = downloadFile(offset);
        if (fileState != null) {
            if (fileState.file != null) {
                cache.put(key, fileState.file, fileState.totalSize, fileState.downloadedSize);
                if (fileState.totalSize == fileState.downloadedSize) {
                    fileDownloadCallback.onSuccess(fileState.file.getAbsolutePath());
                    responseLog.responseHeaders.add(new ApiLogRequestHeader("From Cache", "false"));
                    InAppStoryManager.sendApiResponseLog(responseLog);
                } else {
                    fileDownloadCallback.onError(-1, "Partial content");
                }
            } else {
                fileDownloadCallback.onError(-1, "File haven't downloaded");
            }
        }
        return fileState;
    }

    private void releaseStreamAndFile(FileOutputStream fileOutputStream, FileLock lock) throws IOException {
        fileOutputStream.flush();
        try {
            lock.release();
        } catch (Exception e2) {
        }
        fileOutputStream.close();
    }

    private File generateFileDirsAndGetOutputFile() throws Exception {
        File outputFile = new File(getDownloadFilePath());
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
