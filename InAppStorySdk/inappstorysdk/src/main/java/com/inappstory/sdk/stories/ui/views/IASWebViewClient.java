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
                Downloader.downloadOrGetFile(url, true, cache, null, null);
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

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        try {
            WebResourceResponse response = getChangedResponse(request.getUrl().toString());
            if (response != null) return response;
        } catch (FileNotFoundException ignored) {
        }
        return super.shouldInterceptRequest(view, request);
    }
}