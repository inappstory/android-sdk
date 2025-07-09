package com.inappstory.sdk.stories.ui.views;

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.stories.cache.usecases.StoryVODResourceFileUseCase;
import com.inappstory.sdk.stories.cache.usecases.StoryVODResourceFileUseCaseResult;
import com.inappstory.sdk.stories.cache.vod.ContentRange;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class IASWebViewClient extends WebViewClient {

    private File getCachedFile(String key) {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return null;
        IASCore core = manager.iasCore();
        try {
            return core.contentLoader().getCommonCache().getFullFile(key);
        } catch (Exception e) {
            core.exceptionManager().createExceptionLog(e);
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
            file = getCachedFile(FilesDownloader.deleteQueryArgumentsFromUrlOld(url, true));
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
                return response;
            } catch (Exception e) {
                InAppStoryManager.handleException(e);
            }
        }
        return response;
    }

    private WebResourceResponse parseVODRequest(WebResourceRequest request) {
        String url = request.getUrl().toString();
        String vodAsset = "vod-asset/";
        int indexOf = url.indexOf(vodAsset);
        if (indexOf > -1) {
            if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
                Map<String, String> headers = new HashMap<String, String>() {{
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "E, dd MMM yyyy kk:mm:ss",
                            Locale.US);
                    put("Connection", "close");
                    put("Content-Type", "text/plain");
                    put("Date", formatter.format(new Date()) + " GMT");
                    put("Access-Control-Allow-Origin", "*");
                    put("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
                    put("Access-Control-Max-Age", "600");
                    put("Access-Control-Allow-Credentials", "true");
                    put("Access-Control-Allow-Headers", "accept, authorization, Content-Type, range");
                    put("Via", "1.1 vegur");
                }};
                return new WebResourceResponse("text/plain", "UTF-8", 200, "OK", headers, null);
            }
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
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return null;
        IASCore core = manager.iasCore();
        VODCacheJournalItem item = core.contentLoader().filesDownloadManager().getVodCacheJournal().getItem(uniqueKey);
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
                    core,
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
        try {

            WebResourceResponse response = parseVODRequest(request);
            if (response == null)
                response = getChangedResponse(request.getUrl().toString());
            if (response != null) {
                return response;
            } else {
                Log.e("shouldInterceptRequest", request.getUrl().toString() + " error");
            }
        } catch (Exception e) {

        }
        return super.shouldInterceptRequest(view, request);
    }


}