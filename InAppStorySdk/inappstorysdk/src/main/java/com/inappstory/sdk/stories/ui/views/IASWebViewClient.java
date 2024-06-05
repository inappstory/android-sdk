package com.inappstory.sdk.stories.ui.views;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;
import com.inappstory.sdk.stories.cache.vod.VODDownloader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IASWebViewClient extends WebViewClient {

    private File getCachedFile(String url, String key) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        LruDiskCache cache = service.getCommonCache();
        try {
            File cachedFile = cache.getFullFile(key);
            if (cachedFile == null) {
                // Downloader.downloadOrGetFile(url, true, cache, null, null);
                return null;
            }
            return cachedFile;
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            return null;
        }
    }

    private File getFileByUrl(String url) {
        String filePath = null;
        File file = null;
        if (url.startsWith("http://file-assets")) {
            filePath = Uri.parse(url
                    .replace("http://file-assets", "file://")
            ).getPath();
        } else if (url.startsWith("file://")) {
            filePath = Uri.parse(url).getPath();
        }
        if (filePath != null) {
            file = new File(filePath);
        } else if (!url.startsWith("data:") && URLUtil.isValidUrl(url)) {
            // skip any data URI scheme (data:content/type;)
            file = getCachedFile(url, Downloader.deleteQueryArgumentsFromUrlOld(url, true));
        }
        return file;
    }

    protected WebResourceResponse getChangedResponse(String url) throws FileNotFoundException {
        File file = getFileByUrl(url);
        WebResourceResponse response = null;
        if (file != null && file.exists()) {
            try {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath())
                );
                if (mimeType == null || mimeType.isEmpty()) {
                    mimeType = "application/octet-stream";
                }

                response = new WebResourceResponse(mimeType, "BINARY",
                        new FileInputStream(file));
                Map<String, String> currentHeaders = response.getResponseHeaders();
                if (currentHeaders == null) currentHeaders = new HashMap<>();
                HashMap<String, String> newHeaders = new HashMap<>(currentHeaders);
                newHeaders.put("Access-Control-Allow-Origin", "*");
                response.setResponseHeaders(newHeaders);
            } catch (Exception e) {
                InAppStoryService.createExceptionLog(e);
            }
        }
        return response;
    }

    private WebResourceResponse parseVODRequest(WebResourceRequest request, Context context) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        String url = request.getUrl().toString();
        String vodAsset = "vod-asset/";
        int indexOf = url.indexOf(vodAsset);
        if (indexOf > -1) {
            String key = url.substring(indexOf + vodAsset.length());
            VODCacheJournalItem item = service.getFilesDownloadManager().vodCacheJournal.getItem(key);
            if (item == null) return null;

            long startRange = -1;
            long endRange = -1;
            Map<String, String> headers = request.getRequestHeaders();
            String rangeHeader = headers.get("range");
            if (rangeHeader != null) {
                Pair<Long, Long> range = getRange(rangeHeader);
                startRange = range.first;
                endRange = range.second;
            }
            try {
                byte[] bytes = VODDownloader.downloadBytes(
                        item.getUrl(),
                        startRange,
                        endRange,
                        context.getCacheDir().getFreeSpace()
                );
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        MimeTypeMap.getFileExtensionFromUrl(item.getUrl())
                );
                Log.e("VODTest", item.getUrl() + " " + startRange + " " + endRange);
                return new WebResourceResponse(
                        mimeType,
                        "BINARY",
                        new ByteArrayInputStream(bytes)
                );
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        try {
            WebResourceResponse response = parseVODRequest(request, view.getContext());
            if (response == null)
                response = getChangedResponse(request.getUrl().toString());
            if (response != null) return response;
        } catch (Exception e) {

        }
        return super.shouldInterceptRequest(view, request);
    }

    private Pair<Long, Long> getRange(String rangeHeader) {
        String rangeReplaced = rangeHeader.replaceAll("[^0-9]+", " ").trim();
        String[] ranges = rangeReplaced.split(" ");
        long start = -1;
        try {
            start = Long.parseLong(ranges[0]);
        } catch (Exception e) {

        }
        long end = -1;
        try {
            end = Long.parseLong(ranges[1]);
        } catch (Exception e) {

        }
        return new Pair<>(start, end);
    }
}