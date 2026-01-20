package com.inappstory.sdk.inappmessage.ui.reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.core.utils.IWebViewLogger;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.utils.OnSwipeTouchListener;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IAMWebViewController implements IAMWebViewActions {
    private boolean clientIsSet = false;

    private IAMWebView webView;

    public void contentIdWithIndex(ContentIdWithIndex contentIdWithIndex) {
        this.contentIdWithIndex = contentIdWithIndex;
    }

    private ContentIdWithIndex contentIdWithIndex = null;


    private IWebViewLogger webViewLogger = new IWebViewLogger() {
        @Override
        public void logConsole(ConsoleMessage consoleMessage) {
            if (contentIdWithIndex != null && webView != null && webView.isAttachedToWindow()) {
                webView.sendWebConsoleLog(
                        consoleMessage,
                        Integer.toString(contentIdWithIndex.id()),
                        1,
                        contentIdWithIndex.index()
                );
            }
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_IAM_CONSOLE,
                    consoleMessage.messageLevel().name() + ": "
                            + consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId()
            );
        }

        @Override
        public void logMethod(String payload) {
            if (contentIdWithIndex != null)
                InAppStoryManager.showDLog(
                        LoggerTags.IAS_IAM_JS_CALL,
                        contentIdWithIndex.id() + " " + contentIdWithIndex.index() + " " + payload
                );
        }

        @Override
        public void logJSCall(String payload) {

        }
    };


    public void slideInCache(String slideStatus) {
        IAMWebView iamWebView = webView;
        if (iamWebView != null && iamWebView.isAttachedToWindow()) {

        }
        logMethod("slideInCache " + slideStatus);
    }

    private String oldEscape(String raw) {
        String escaped = raw
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\n", " ")
                .replaceAll("\r", " ");
        return escaped;
    }

    @Override
    public void setSdkClientVariables(String clientVariables) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                Map<String, String> extraOptions = ((IASDataSettingsHolder) core.settingsAPI()).options();
                try {
                    String extraOptionsString = JsonParser.stringMapToEscapedJsonString(extraOptions);
                    IAMWebView iamWebView = webView;
                    if (iamWebView != null && iamWebView.isAttachedToWindow()) {

                    }
                    logMethod("set_sdk_client_variables");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showSlides(List<String> slides, String cardAppearance, int index) {
        IAMWebView iamWebView = webView;
        if (iamWebView != null && iamWebView.isAttachedToWindow()) {

        }
        logMethod("showSlides " + slides.size());
    }

    @Override
    public void setSlideInCacheStatus(String slideStatus) {

    }


    @Override
    public void loadSlide(String content) {
        IAMWebView iamWebView = webView;
        if (iamWebView != null && iamWebView.isAttachedToWindow()) {

        }
    }


    @Override
    public void pauseSlide() {
        IAMWebView iamWebView = webView;
        if (iamWebView != null && iamWebView.isAttachedToWindow()) {

        }

        logMethod("story_slide_pause");
    }

    @Override
    public void startSlide() {
        IAMWebView iamWebView = webView;
        if (iamWebView != null && iamWebView.isAttachedToWindow()) {

        }
        logMethod("story_slide_start");
    }

    @Override
    public void resumeSlide() {
        IAMWebView iamWebView = webView;
        if (iamWebView != null && iamWebView.isAttachedToWindow()) {

        }

        logMethod("story_slide_resume");
    }


    @Override
    public void stopSlide() {
        IAMWebView iamWebView = webView;
        if (iamWebView != null && iamWebView.isAttachedToWindow()) {

        }
        iamWebView.stopSlide();
        logMethod("story_slide_stop");
    }

    @Override
    public void loadJsApiResponse(String result, String cb) {
        IAMWebView iamWebView = webView;
        if (iamWebView != null && iamWebView.isAttachedToWindow()) {

        }
        evaluateJavascript(cb + "('" + StringsUtils.escapeSingleQuotes(result) + "');", null);
    }

    @Override
    public void initClient(IAMReaderJavascriptInterface javascriptInterface) {

    }


}
