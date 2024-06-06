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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VODDownloader {
    public WebResourceResponse getWebResourceResponse(
            String rangeHeader,
            String uniqueKey,
            Context context
    ) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        VODCacheJournalItem item = service.getFilesDownloadManager().vodCacheJournal.getItem(uniqueKey);
        if (item == null) return null;

        ContentRange range;
        if (rangeHeader != null) {
            range = getRange(rangeHeader, item.fullSize);
        } else {
            range = new ContentRange(0, item.fullSize, item.fullSize);
        }

        try {
            boolean hasInCache = false;//item.hasPart(startRange, endRange);
            byte[] bytes = null;
            if (hasInCache) {
                bytes = getBytesFromFile(range, item.filePath);
            } else {
                Pair<ContentRange, byte[]> data = downloadBytes(
                        item.getUrl(),
                        range.start(),
                        range.end(),
                        context.getCacheDir().getFreeSpace()
                );
                if (data == null) return null;
                bytes = data.second;
                range = data.first;
                item.fullSize = range.length();
                item.addPart(range.start(), range.end());
            }
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(item.getUrl())
            );
            Log.e("VODTest", item.getUrl() + " " + range.start() + " " + range.end());


            WebResourceResponse response = new WebResourceResponse(
                    mimeType,
                    "BINARY",
                    new ByteArrayInputStream(bytes)
            );
            response.setStatusCodeAndReasonPhrase(206, "Partial Content");
            Map<String, String> currentHeaders = response.getResponseHeaders();
            if (currentHeaders == null) currentHeaders = new HashMap<>();
            HashMap<String, String> newHeaders = new HashMap<>(currentHeaders);
            if (hasInCache)
                newHeaders.put("X-VOD-From-Cache", "");
            newHeaders.put("Content-Range", "bytes=" + range.start() + "-" + range.end() + "/" + range.length());
            newHeaders.put("Content-Length", "" + (bytes.length));
            newHeaders.put("Content-Type", mimeType);
            response.setResponseHeaders(newHeaders);
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] getBytesFromFile(ContentRange contentRange, String filename) {
        return null;
    }

    private Pair<ContentRange, byte[]> downloadBytes(
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
            contentRange = getRange(rangeHeader, sz);
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


    private ContentRange getRange(String rangeHeader, long contentLength) {
        String[] sections = rangeHeader.split("/");
        String rangeSection = "";
        rangeSection = sections[0];

        String rangeReplaced = rangeSection.replaceAll("[^0-9]+", " ").trim();
        String[] ranges = rangeReplaced.split(" ");
        long start = -1;
        long length = 0;
        try {
            start = Long.parseLong(ranges[0]);
        } catch (Exception e) {

        }
        long end = -1;
        try {
            end = Long.parseLong(ranges[1]);
        } catch (Exception e) {

        }
        if (sections.length == 2) {
            length = Long.parseLong(sections[1]);
        } else {
            if (end != -1) {
                length = end;
            } else {
                length = contentLength;
            }
        }
        return new ContentRange(start, end, length);
    }
}
