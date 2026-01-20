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
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.core.utils.IWebViewLogger;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.utils.OnSwipeTouchListener;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

public class IAMWebView extends IASWebView implements IAMWebViewActions {
    private boolean clientIsSet = false;

    public void setWebViewLogger(IWebViewLogger webViewLogger) {
        this.webViewLogger = webViewLogger;
    }

    private IWebViewLogger webViewLogger;

    @Override
    public void setSdkClientVariables(
            String clientVariables
    ) {

        /*InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                Map<String, String> extraOptions = ((IASDataSettingsHolder) core.settingsAPI()).options();
                try {
                    String extraOptionsString = JsonParser.stringMapToEscapedJsonString(extraOptions);
                    loadUrl("javascript:window.set_sdk_client_variables('" +
                            StringsUtils.getEscapedString(StringsUtils.escapeSingleQuotes(extraOptionsString))
                            + "')");
                    logMethod("set_sdk_client_variables");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
        loadUrl("javascript:window.set_sdk_client_variables('" +
                StringsUtils.getEscapedString(StringsUtils.escapeSingleQuotes(clientVariables)) +
                "')");
    }

    @Override
    public void setSlideInCacheStatus(String slideStatus) {
        String url = "javascript:window.slide_in_cache('" + slideStatus + "')";
        loadUrl(url);
        //logMethod("slideInCache " + slideStatus);
    }

    private String oldEscape(String raw) {
        String escaped = raw
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\n", " ")
                .replaceAll("\r", " ");
        return escaped;
    }

    public void showSlides(List<String> slides, String cardAppearance, int index) {
        List<String> escapedSlides = new ArrayList<>();
        for (String slide : slides) {
            escapedSlides.add(oldEscape(slide));
        }
        String slideArray = "[\"" + TextUtils.join("\",\"", escapedSlides) + "\"]";
        String url = "javascript:window.show_slides(" + slideArray + ",\"" +
                StringsUtils.getEscapedString(StringsUtils.escapeSingleQuotes(cardAppearance))
                + "\", " + index + ")";
        loadUrl(url);
        //logMethod("showSlides " + slides.size());
    }


    public IAMWebView(
            @NonNull Context context
    ) {
        super(context);
    }

    public IAMWebView(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public IAMWebView(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeUp() {
                swipeUp();
            }
        });
    }

    @Override
    public void loadSlide(String content) {
        String newContent = setDir(content, getContext());
        loadDataWithBaseURL(
                "file:///data/",
                newContent,
                "text/html; charset=utf-8",
                "UTF-8",
                null
        );
    }

    @Override
    public void pauseSlide() {
        loadUrl("javascript:(function(){" +
                "if ('story_slide_pause' in window) " +
                "{" +
                " window.story_slide_pause(); " +
                "}" +
                "})()");
    }

    @Override
    public void startSlide() {
        loadUrl("javascript:(function(){" +
                "if ('story_slide_start' in window) " +
                "{" +
                " window.story_slide_start('{\"muted\": false}');" +
                "}" +
                "})()");
    }

    @Override
    public void resumeSlide() {
        loadUrl("javascript:(function() {" +
                "if ('story_slide_resume' in window) " +
                "{" +
                " window.story_slide_resume(); " +
                "}" +
                "})()");
    }

    @Override
    public void stopSlide() {
        String funAfterCheck = "story_slide_stop('{\"prepareForRestart\": false}'); ";
        loadUrl("javascript:(function(){" +
                "if ('story_slide_stop' in window) " +
                "{" +
                " window." + funAfterCheck +
                " }" +
                "})()");
    }

    @Override
    public void swipeUp() {
        loadUrl("javascript:window.story_slide_swipe_up()");
        if (webViewLogger != null) webViewLogger.logMethod("story_slide_swipe_up");
    }

    @Override
    public void loadJsApiResponse(String result, String cb) {
        evaluateJavascript(cb + "('" + StringsUtils.escapeSingleQuotes(result) + "');", null);
    }

    @Override
    public void initClient(
            IAMReaderJavascriptInterface javascriptInterface
    ) {
        if (!clientIsSet) {
            addJavascriptInterface(
                    javascriptInterface,
                    "Android"
            );
            setWebViewClient(new IASWebViewClient());
            setWebChromeClient(new WebChromeClient() {
                @Nullable
                @Override
                public Bitmap getDefaultVideoPoster() {
                    if (super.getDefaultVideoPoster() == null) {
                        Bitmap bmp = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bmp);
                        canvas.drawColor(Color.WHITE);
                        return bmp;
                    } else {
                        return super.getDefaultVideoPoster();
                    }
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {

                }

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    if (webViewLogger != null) webViewLogger.logConsole(consoleMessage);
                    return super.onConsoleMessage(consoleMessage);
                }
            });

        }
        clientIsSet = true;
    }


    @Override
    public void destroyView() {
        super.destroyView();
    }


}
