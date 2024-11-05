package com.inappstory.sdk.stories.ui.views;

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.usecases.StoryVODResourceFileUseCase;
import com.inappstory.sdk.stories.cache.usecases.StoryVODResourceFileUseCaseResult;
import com.inappstory.sdk.stories.cache.vod.ContentRange;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;


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
                Log.e("GameInterceptRequest", "FileExist " + file.getAbsolutePath());
            } catch (Exception e) {
                Log.e("GameInterceptRequest", "Exception :" + e.getCause() + "\n" + e.getMessage());
                InAppStoryService.createExceptionLog(e);
            }
        }
        return response;
    }

    private WebResourceResponse parseVODRequest(WebResourceRequest request) {
        String url = request.getUrl().toString();
        String vodAsset = "vod-asset/";
        int indexOf = url.indexOf(vodAsset);
        if (indexOf > -1) {
            String key = url.substring(indexOf + vodAsset.length());
            Log.e("VOD_req", key);
            Map<String, String> headers = request.getRequestHeaders();
            String rangeHeader = headers.get("range");
            WebResourceResponse response = getWebResourceResponse(rangeHeader, key);
            return response;
        }
        return null;
    }


    private WebResourceResponse getWebResourceResponse(
            String rangeHeader,
            String uniqueKey
    ) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        VODCacheJournalItem item = service.getFilesDownloadManager().getVodCacheJournal().getItem(uniqueKey);
        if (item == null) return null;

        ContentRange range;

        if (rangeHeader != null) {
            range = StringsUtils.getRange(rangeHeader, item.getFullSize());
        } else {
            range = new ContentRange(0, item.getFullSize(), item.getFullSize());
        }

        Log.e("WebProfiling", "getWebResourceResponse Req " + item.getUrl() + " " + rangeHeader + " " + System.currentTimeMillis());
        try {
            StoryVODResourceFileUseCaseResult res = new StoryVODResourceFileUseCase(
                    service.getFilesDownloadManager(),
                    item.getUrl(),
                    uniqueKey,
                    range.start(),
                    range.end()
            ).getFile();
            if (res == null) return null;
            File file = res.file();
            range = res.range();
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(item.getUrl())
            );
            Log.e("VODTest", item.getUrl() + " " + range.start() + " " + range.end());


            WebResourceResponse response = new WebResourceResponse(
                    mimeType,
                    "BINARY",
                    new FileInputStream(file)
            );
            response.setStatusCodeAndReasonPhrase(206, "Partial Content");
            Map<String, String> currentHeaders = response.getResponseHeaders();
            if (currentHeaders == null) currentHeaders = new HashMap<>();
            HashMap<String, String> newHeaders = new HashMap<>(currentHeaders);
            Log.e("VOD_Resource", item.getUrl() + " " +
                    (range.start() + "-" + range.end() + "/" + range.length())
                    + " Cached:" + res.cached());
            if (res.cached())
                newHeaders.put("X-VOD-From-Cache", "");
            newHeaders.put("Access-Control-Allow-Origin", "*");
            newHeaders.put("Content-Range", "bytes=" + range.start() + "-" + range.end() + "/" + range.length());
            newHeaders.put("Content-Length", "" + (range.length()));
            newHeaders.put("Content-Type", mimeType);
            response.setResponseHeaders(newHeaders);
            return response;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Log.e("GameInterceptRequest", request.getUrl().toString());
        try {
            WebResourceResponse response = parseVODRequest(request);
            if (response == null)
                response = getChangedResponse(request.getUrl().toString());
            if (response != null) return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.shouldInterceptRequest(view, request);
    }


}