package com.inappstory.sdk.stories.cache.vod;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.utils.ConnectionHeadersMap;
import com.inappstory.sdk.network.utils.ResponseStringFromStream;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.StoryVODResourceFileUseCase;
import com.inappstory.sdk.stories.cache.usecases.StoryVODResourceFileUseCaseResult;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class VODDownloader {
    public boolean putBytesToFile(
            long position,
            byte[] bytes,
            String filePath
    ) {
        File file = new File(filePath);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().write(byteBuffer, position);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Pair<ContentRange, byte[]> getBytesFromFile(ContentRange contentRange, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        long bytesAmount = contentRange.end() - contentRange.start();
        if (bytesAmount > (long) Integer.MAX_VALUE) {
            return null;
        }
        ByteBuffer bytes = ByteBuffer.allocateDirect((int) bytesAmount);
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.getChannel().read(bytes, contentRange.start());
            return new Pair<>(contentRange, bytes.array());
        } catch (IOException e) {

        }
        return null;
    }

    public Pair<ContentRange, byte[]> downloadBytes(
            String url,
            long from,
            long to,
            long freeSpace
    ) throws Exception {
        URL urlS = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlS.openConnection();
        urlConnection.setConnectTimeout(300000);
        urlConnection.setReadTimeout(300000);
        urlConnection.setRequestMethod("GET");
        if (InAppStoryManager.getNetworkClient() != null) {
            urlConnection.setRequestProperty("User-Agent", InAppStoryManager.getNetworkClient().userAgent);
        }
        if (from > 0) {
            if (to > 0) {
                urlConnection.setRequestProperty("Range", "bytes=" + from + "-" + to);
            } else {
                urlConnection.setRequestProperty("Range", "bytes=" + from + "-");
            }
        } else {
            if (to > 0) {
                urlConnection.setRequestProperty("Range", "bytes=" + 0 + "-" + to);
            }
        }
        ContentRange contentRange = new ContentRange(from, to, to);
        //String curl = toCurlRequest(urlConnection, null);
        try {
            urlConnection.connect();
        } catch (Exception e) {
            return null;
        }
        int status = urlConnection.getResponseCode();
        HashMap<String, String> headers = new HashMap<>();

        int sz = urlConnection.getContentLength();
        if (freeSpace > 0 && sz > freeSpace) {
            urlConnection.disconnect();
            return null;
        }
       /* for (String headerKey : urlConnection.getHeaderFields().keySet()) {
            if (headerKey == null) continue;
            if (urlConnection.getHeaderFields().get(headerKey).isEmpty()) continue;
            headers.put(headerKey, urlConnection.getHeaderFields().get(headerKey).get(0));
            if (headerKey.equalsIgnoreCase("Content-Range")) {
                String rangeHeader = urlConnection.getHeaderFields().get(headerKey).get(0);
                if (!rangeHeader.equalsIgnoreCase("none")) {
                    try {
                        sz = Integer.parseInt(rangeHeader.split("/")[1]);
                    } catch (Exception e) {

                    }
                }
            }
        }*/
        String decompression = null;
        HashMap<String, String> responseHeaders = new ConnectionHeadersMap().get(urlConnection);
        if (responseHeaders.containsKey("Content-Encoding")) {
            decompression = responseHeaders.get("Content-Encoding");
        }
        if (responseHeaders.containsKey("content-encoding")) {
            decompression = responseHeaders.get("content-encoding");
        }
        String rangeHeader = responseHeaders.get("Content-Range");
        if (rangeHeader != null) {
            contentRange = StringsUtils.getRange(rangeHeader, sz);
        }
        ResponseStringFromStream responseStringFromStream = new ResponseStringFromStream();
        if (status > 350) {
            return null;
        }
        byte[] data = new byte[1024];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        InputStream inputStream = responseStringFromStream.getInputStream(
                urlConnection.getInputStream(),
                decompression
        );
        int nRead;

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new Pair<>(contentRange, buffer.toByteArray());
    }
}
