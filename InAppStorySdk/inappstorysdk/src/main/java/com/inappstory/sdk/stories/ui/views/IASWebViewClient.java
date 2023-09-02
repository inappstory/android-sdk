package com.inappstory.sdk.stories.ui.views;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;


public class IASWebViewClient extends WebViewClient {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        try {
            if (request.getUrl().toString().startsWith("http://file-assets")) {
                String filePath = Uri.parse(request
                        .getUrl()
                        .toString()
                        .replace("http://file-assets", "file://")
                ).getPath();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                MimeTypeMap.getFileExtensionFromUrl(filePath)
                        );
                        WebResourceResponse response = new WebResourceResponse(
                                mimeType,
                                "utf-8",
                                new FileInputStream(file)
                        );
                        Map<String, String> currentHeaders = response.getResponseHeaders();
                        if (currentHeaders == null) currentHeaders = new HashMap<>();
                        HashMap<String, String> newHeaders = new HashMap<>(currentHeaders);
                        newHeaders.put("Access-Control-Allow-Origin", "*");
                        response.setResponseHeaders(newHeaders);
                        return response;
                    }


                }
            }
        } catch (FileNotFoundException ignored) {
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        try {
            if (url.startsWith("http://file-assets")) {
                String filePath = Uri.parse(
                        url.replace("http://file-assets", "file://")
                ).getPath();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                MimeTypeMap.getFileExtensionFromUrl(filePath)
                        );
                        return new WebResourceResponse(
                                mimeType,
                                "utf-8",
                                new FileInputStream(file)
                        );
                    }
                }
            }
        } catch (FileNotFoundException ignored) {
        }
        return super.shouldInterceptRequest(view, url);
    }

}