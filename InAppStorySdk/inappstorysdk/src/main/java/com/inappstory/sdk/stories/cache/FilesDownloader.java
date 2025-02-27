package com.inappstory.sdk.stories.cache;

import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
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
                -1,
                finishCallback
        );
    }


    public DownloadFileState downloadFile(
            String url,
            File outputFile,
            FileLoadProgressCallback callback,
            ApiLogResponse apiLogResponse,
            DownloadInterruption interruption,
            long downloadOffset,
            long downloadLimit,
            FinishDownloadFileCallback finishCallback
    ) throws Exception {
        DownloadFileState state = null;
        FilesDownloadManager manager = core.contentLoader().filesDownloadManager();
        if (url.contains("widgets/poll.css")) {
            InAppStoryManager.showDLog("downloadPollCss", "check " + url);
        }
        if (manager != null && !manager.addFinishCallback(url, finishCallback))
            return null;
        if (url.contains("widgets/poll.css")) {
            InAppStoryManager.showDLog("downloadPollCss", "load " + url);
        }
        outputFile.getParentFile().mkdirs();
        if (!outputFile.exists())
            outputFile.createNewFile();

        URL urlS = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlS.openConnection();
        urlConnection.setRequestProperty("Accept-Encoding", "br, gzip");
        urlConnection.setConnectTimeout(300000);
        urlConnection.setReadTimeout(300000);
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", core.network().userAgent());
        if (downloadOffset > 0) {
            if (downloadLimit > 0) {
                urlConnection.setRequestProperty("Range", "bytes=" + downloadOffset + "-" + downloadLimit);
            } else {
                urlConnection.setRequestProperty("Range", "bytes=" + downloadOffset + "-");
            }
        } else {
            if (downloadLimit > 0) {
                urlConnection.setRequestProperty("Range", "bytes=" + 0 + "-" + downloadLimit);
            }
        }
        //String curl = toCurlRequest(urlConnection, null);
        try {
            urlConnection.connect();
        } catch (Exception e) {
            if (manager != null)
                manager.invokeFinishCallbacks(url, null);
            return null;
        }
        int status = urlConnection.getResponseCode();
        HashMap<String, String> headers = new HashMap<>();

        long sz = urlConnection.getContentLength();
        long freeSpace = outputFile.getFreeSpace();
        if (freeSpace > 0 && sz > freeSpace) {
            urlConnection.disconnect();
            if (manager != null)
                manager.invokeFinishCallbacks(url, null);
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
            if (manager != null)
                manager.invokeFinishCallbacks(url, null);
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
            core.keyValueStorage().saveString(outputFile.getName(), contentType);
        else
            core.keyValueStorage().saveString(outputFile.getName(), "image/jpeg");
        byte[] buffer = new byte[1024];
        int bufferLength = 0;
        int cnt = 0;
        try {
            if (interruption != null) {
                Log.e("ArchiveUseCase", "FilesDownloader " + url + " " + interruption);
            }
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                if (interruption != null && interruption.active) {
                    releaseStreamAndFile(fileOutputStream, lock);
                    if (allowPartial)
                        state = new DownloadFileState(outputFile, sz, outputFile.length());
                    if (manager != null)
                        manager.invokeFinishCallbacks(url, state);
                    return state;
                } else {
                    fileOutputStream.write(buffer, 0, bufferLength);
                    cnt += bufferLength;
                    if (callback != null)
                        callback.onProgress(downloadOffset + cnt, sz);
                }
            }
            releaseStreamAndFile(fileOutputStream, lock);
            apiLogResponse.generateFile(status, outputFile.getAbsolutePath(), headers);
            state = new DownloadFileState(outputFile, outputFile.length(), outputFile.length());
        } catch (Exception e) {
            releaseStreamAndFile(fileOutputStream, lock);
            if (allowPartial) {
                state = new DownloadFileState(outputFile, sz, outputFile.length());
            }
        }
        if (manager != null)
            manager.invokeFinishCallbacks(url, state);
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
