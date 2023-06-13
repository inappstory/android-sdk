package com.inappstory.sdk.stories.ui.views;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class IASWebViewClient extends WebViewClient {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        if (URLUtil.isFileUrl(request.getUrl().toString())) {
            String type =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            MimeTypeMap.getFileExtensionFromUrl(request.getUrl().toString())
                    );
            if (type != null && type.startsWith("image"))
                Log.d("InAppStory_SDK_Game", request.getUrl().toString() + " " + type);

        }
        // Log.e("shouldInterceptRequest", request.getUrl().toString() + " " + request.getRequestHeaders().toString());
        return super.shouldInterceptRequest(view, request);
    }

}