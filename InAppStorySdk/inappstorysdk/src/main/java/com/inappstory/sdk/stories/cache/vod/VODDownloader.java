package com.inappstory.sdk.stories.cache.vod;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.network.utils.ConnectionHeadersMap;
import com.inappstory.sdk.network.utils.ResponseStringFromStream;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class VODDownloader {
    public static byte[] downloadBytes(
            String url,
            long downloadOffset,
            long downloadLimit,
            long freeSpace,
            FilesDownloadManager manager
    ) throws Exception {
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
        if (freeSpace > 0 && sz > freeSpace) {
            urlConnection.disconnect();
            return null;
        }
        for (String headerKey : urlConnection.getHeaderFields().keySet()) {
            if (headerKey == null) continue;
            if (urlConnection.getHeaderFields().get(headerKey).isEmpty()) continue;
            headers.put(headerKey, urlConnection.getHeaderFields().get(headerKey).get(0));
            if (headerKey.equalsIgnoreCase("Content-Range")) {
                String rangeHeader = urlConnection.getHeaderFields().get(headerKey).get(0);
                if (!rangeHeader.equalsIgnoreCase("none")) {
                    try {
                        sz = Long.parseLong(rangeHeader.split("/")[1]);
                    } catch (Exception e) {

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
        ResponseStringFromStream responseStringFromStream = new ResponseStringFromStream();
        if (status > 350) {
            return null;
        }
        InputStream inputStream = responseStringFromStream.getInputStream(
                urlConnection.getInputStream(),
                decompression
        );
        return null;
    }
}
