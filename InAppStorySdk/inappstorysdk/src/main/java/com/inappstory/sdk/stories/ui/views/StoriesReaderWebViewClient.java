package com.inappstory.sdk.stories.ui.views;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;

import java.io.File;
import java.io.FileInputStream;

public class StoriesReaderWebViewClient extends IASWebViewClient {
    StoriesViewManager manager;

    public StoriesReaderWebViewClient() {

    }

    public StoriesReaderWebViewClient(StoriesViewManager manager) {
        this.manager = manager;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        String img = url;
        if (img.startsWith("data:text/html;") || !URLUtil.isValidUrl(img) || manager == null)
            return super.shouldInterceptRequest(view, url);
        InAppStoryManager.showDLog("webView_int_url", url);
        File file = manager.getCurrentFile(img);
        if (file != null && file.exists()) {
            try {
                Response response = new Request.Builder().head().url(url).build().execute();
                String ctType = response.headers.get("Content-Type");
                return new WebResourceResponse(ctType, "BINARY",
                        new FileInputStream(file));
            } catch (Exception e) {
                InAppStoryService.createExceptionLog(e);
                return super.shouldInterceptRequest(view, url);
            }
        } else
            return super.shouldInterceptRequest(view, url);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest
            request) {
        String img = request.getUrl().toString();
        if (img.startsWith("data:text/html;") || !URLUtil.isValidUrl(img))
            return super.shouldInterceptRequest(view, request);
        InAppStoryManager.showDLog("webView_int_resource", img);

        File file = manager.getCurrentFile(img);
        if (file != null && file.exists()) {
            try {
                Response response = new Request.Builder().head().url(request.getUrl().toString()).build().execute();
                String ctType = response.headers.get("Content-Type");
                return new WebResourceResponse(ctType, "BINARY",
                        new FileInputStream(file));
            } catch (Exception e) {
                InAppStoryService.createExceptionLog(e);
                return super.shouldInterceptRequest(view, request);
            }
        } else
            return super.shouldInterceptRequest(view, request);
    }


    @Override
    public void onPageFinished(WebView view, String url) {

    }
}