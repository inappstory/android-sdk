package com.inappstory.sdk.stories.ui.views;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class StoriesReaderWebViewClient extends IASWebViewClient {
    StoriesViewManager manager;

    public StoriesReaderWebViewClient() {

    }

    public StoriesReaderWebViewClient(StoriesViewManager manager) {
        this.manager = manager;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse response = getWebResourceResponse(url);
        return response != null ? response :
                super.shouldInterceptRequest(view, url);
    }

    private WebResourceResponse getWebResourceResponse(String url) {
        String img = Downloader.cropUrl(url, true);
        WebResourceResponse webResourceResponse = null;
        try {
            webResourceResponse = getChangedResponse(url);
        } catch (FileNotFoundException e) {

        }
        if (webResourceResponse == null) {
            if (!img.startsWith("data:text/html;") && URLUtil.isValidUrl(img) && manager != null) {
                File file = manager.getCachedFile(url, img);
                if (file != null && file.exists()) {
                    try {
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath())
                        );
                        if (mimeType == null || mimeType.isEmpty()) {
                            Response response = new Request.Builder().head().url(url).build().execute();
                            if (response.headers != null)
                                mimeType = response.headers.get("Content-Type");
                        }
                        webResourceResponse = new WebResourceResponse(mimeType != null ? mimeType : "", "BINARY",
                                new FileInputStream(file));
                    } catch (Exception e) {
                        InAppStoryService.createExceptionLog(e);
                    }
                }
            }
        }
        if (webResourceResponse == null)
            return null;
        InAppStoryManager.showDLog("webView_int_resource", img);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Map<String, String> currentHeaders = webResourceResponse.getResponseHeaders();
            if (currentHeaders == null) currentHeaders = new HashMap<>();
            HashMap<String, String> newHeaders = new HashMap<>(currentHeaders);
            newHeaders.put("Access-Control-Allow-Origin", "*");
            webResourceResponse.setResponseHeaders(newHeaders);
        }
        return webResourceResponse;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        WebResourceResponse response = getWebResourceResponse(request.getUrl().toString());
        return response != null ? response :
                super.shouldInterceptRequest(view, request.getUrl().toString());
    }


    @Override
    public void onPageFinished(WebView view, String url) {

    }
}