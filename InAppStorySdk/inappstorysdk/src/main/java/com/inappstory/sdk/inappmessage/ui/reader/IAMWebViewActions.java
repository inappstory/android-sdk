package com.inappstory.sdk.inappmessage.ui.reader;

import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import java.util.List;

public interface IAMWebViewActions {
    void setSdkClientVariables(
            String clientVariables
    );
    void showSlides(
            List<String> slides,
            String cardAppearance,
            int index
    );

    void setSlideInCacheStatus(String slideStatus);

    void loadSlide(String content);

    void pauseSlide();

    void startSlide();

    void resumeSlide();

    void stopSlide();

    void loadJsApiResponse(String result, String cb);

    void initClient(
            IAMReaderJavascriptInterface javascriptInterface
    );
}
