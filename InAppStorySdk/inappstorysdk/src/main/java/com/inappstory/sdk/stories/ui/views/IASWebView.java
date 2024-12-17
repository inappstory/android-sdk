package com.inappstory.sdk.stories.ui.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.api.models.logs.WebConsoleLog;

import java.util.UUID;

public class IASWebView extends WebView {
    public IASWebView(@NonNull Context context) {
        super(context);
        init();
    }

    public IASWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IASWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void evaluateJavascript(@NonNull String script, @Nullable ValueCallback<String> resultCallback) {
        Log.d("InAppStory_SDK", script);
        super.evaluateJavascript(script, resultCallback);
    }

    protected void init() {
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(getResources().getColor(R.color.black));

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        getSettings().setMinimumFontSize(1);
        getSettings().setTextZoom(100);
        getSettings().setAllowContentAccess(true);
        getSettings().setAllowFileAccess(true);
        //  getSettings().setAllowFileAccessFromFileURLs(true);
        //  getSettings().setAllowUniversalAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSettings().setOffscreenPreRaster(true);
        }

        setClickable(true);
        getSettings().setJavaScriptEnabled(true);
    }

    public void sendWebConsoleLog(
            ConsoleMessage consoleMessage,
            String storyId,
            int slideIndex
    ) {
        WebConsoleLog log = new WebConsoleLog();
        log.timestamp = System.currentTimeMillis();
        log.id = UUID.randomUUID().toString();
        log.logType = consoleMessage.messageLevel().name();
        log.message = consoleMessage.message();
        log.sourceId = consoleMessage.sourceId();
        log.lineNumber = consoleMessage.lineNumber();
        log.storyId = storyId;
        log.slideIndex = slideIndex;
        InAppStoryManager.sendWebConsoleLog(log);
    }

    protected String injectUnselectableStyle(String html) {
        return html.replace("<head>",
                "<head><style>*{" +
                        "-webkit-touch-callout: none;" +
                        "-webkit-user-select: none;" +
                        "-khtml-user-select: none;" +
                        "-moz-user-select: none;" +
                        "-ms-user-select: none;" +
                        "user-select: none;" +
                        "} </style>");
    }

    public void destroyView() {
        removeAllViews();
        clearHistory();
        clearCache(true);
        loadUrl("about:blank");
        removeAllViews();
        destroyDrawingCache();
    }

    public String setDir(String html) {
        try {
            int dir = getContext().getResources().getConfiguration().getLayoutDirection();
            String dirString = (dir == View.LAYOUT_DIRECTION_RTL) ? "rtl" : "ltr";
            return html.replace("{{%dir}}", dirString);
        } catch (Exception e) {
            return html;
        }
    }

}
