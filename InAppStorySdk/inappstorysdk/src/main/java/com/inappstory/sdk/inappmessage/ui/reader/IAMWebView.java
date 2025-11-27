package com.inappstory.sdk.inappmessage.ui.reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ContentViewInteractor;
import com.inappstory.sdk.utils.OnSwipeTouchListener;
import com.inappstory.sdk.utils.StringsUtils;

public class IAMWebView extends IASWebView implements ContentViewInteractor {
    private boolean clientIsSet = false;

    @Override
    public void slideViewModel(IIAMReaderSlideViewModel slideViewModel) {
        this.slideViewModel = slideViewModel;
    }

    private IIAMReaderSlideViewModel slideViewModel;

    public IAMWebView(
            @NonNull Context context
    ) {
        super(context.getApplicationContext());
    }

    public IAMWebView(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context.getApplicationContext(), attrs);
    }

    public IAMWebView(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context.getApplicationContext(), attrs, defStyleAttr);
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

    private void logMethod(String payload) {
        if (slideViewModel == null) return;
        ContentIdWithIndex contentIdWithIndex = slideViewModel.iamId();
        if (contentIdWithIndex != null)
            InAppStoryManager.showDLog("JS_method_call",
                    contentIdWithIndex.id() + " " + contentIdWithIndex.index() + " " + payload);
    }

    @Override
    public void loadSlide(String content) {
        if (slideViewModel == null) return;
        String newContent = setDir(content, getContext());
        loadDataWithBaseURL(
                "file:///data/",
                newContent,
                "text/html; charset=utf-8",
                "UTF-8",
                null
        );
    }

    @Deprecated
    private String temporaryUpdateToWhiteBackground(String html) {
        return updateHead(html, "<style> html { background: white !important; } </style>");
    }

    @Override
    public void replaceSlide(String newContent) {
        evaluateJavascript("(function(){show_slide(\"" + newContent + "\");})()", null);
        logMethod("show_slide");
    }


    @Override
    public void pauseSlide() {
        loadUrl("javascript:(function(){" +
                "if ('story_slide_pause' in window) " +
                "{" +
                " window.story_slide_pause(); " +
                "}" +
                "})()");

        logMethod("story_slide_pause");
    }

    @Override
    public void startSlide(IASCore core) {
        loadUrl("javascript:(function(){" +
                "if ('story_slide_start' in window) " +
                "{" +
                " window.story_slide_start('{\"muted\": false}');" +
                "}" +
                "})()");
        logMethod("story_slide_start");
    }

    @Override
    public void resumeSlide() {
        loadUrl("javascript:(function() {" +
                "if ('story_slide_resume' in window) " +
                "{" +
                " window.story_slide_resume(); " +
                "}" +
                "})()");
        logMethod("story_slide_resume");
    }

    @Override
    public void restartSlide(IASCore core) {
        loadUrl("javascript:(function(){" +
                "if ('story_slide_restart' in window) " +
                "{" +
                " window.story_slide_restart('{\"muted\": false}');" +
                "}" +
                "})()");
        logMethod("story_slide_restart");
    }

    @Override
    public void stopSlide(boolean newPage) {
        String funAfterCheck = "story_slide_stop('{\"prepareForRestart\": false}'); ";
        loadUrl("javascript:(function(){" +
                "if ('story_slide_stop' in window) " +
                "{" +
                " window." + funAfterCheck +
                " }" +
                "})()");
        logMethod("story_slide_stop");
    }

    @Override
    public void swipeUp() {
        loadUrl("javascript:window.story_slide_swipe_up()");
        logMethod("story_slide_swipe_up");
    }

    @Override
    public void clearSlide(int index) {
        if (index < 0) return;
        evaluateJavascript("(function(){clear_slide(" + index + ");})()", null);
        logMethod("clear_slide " + index);
    }

    @Override
    public void loadJsApiResponse(String result, String cb) {
        evaluateJavascript(cb + "('" + StringsUtils.escapeSingleQuotes(result) + "');", null);
    }


    @Override
    public Context getActivityContext() {
        return null;
    }

    @Override
    public void changeSoundStatus(IASCore core) {
        if (((IASDataSettingsHolder) core.settingsAPI()).isSoundOn()) {
            loadUrl("javascript:(function(){story_slide_enable_audio();})()");
        } else {
            loadUrl("javascript:(function(){story_slide_disable_audio();})()");
        }
    }

    @Override
    public void cancelDialog(String id) {
        throw new NotImplementedMethodException();
    }

    @Override
    public void sendDialog(String id, String data) {
        throw new NotImplementedMethodException();
    }

    @Override
    public void destroyView() {
        super.destroyView();
    }

    @Override
    public float getCoordinate() {
        return 0;
    }

    @Override
    public void shareComplete(String stId, boolean success) {
        throw new NotImplementedMethodException();
    }

    @Override
    public void freezeUI() {
        throw new NotImplementedMethodException();
    }

    @Override
    public void unfreezeUI() {
        throw new NotImplementedMethodException();
    }


    @Override
    public void checkIfClientIsSet() {
        if (!clientIsSet) {
            if (slideViewModel == null) return;
            addJavascriptInterface(
                    new IAMReaderJavascriptInterface(slideViewModel),
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
                    ContentIdWithIndex idWithIndex = slideViewModel.iamId();
                    if (idWithIndex != null) {
                        sendWebConsoleLog(
                                consoleMessage,
                                Integer.toString(idWithIndex.id()),
                                1,
                                idWithIndex.index()
                        );
                    }

                    Log.d("InAppStory_SDK_Web", consoleMessage.messageLevel().name() + ": "
                            + consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId());
                    return super.onConsoleMessage(consoleMessage);
                }
            });

        }
        clientIsSet = true;
    }

    @Override
    public void screenshotShare(String id) {
        evaluateJavascript("share_slide_screenshot(\"" + id + "\");", null);
        logMethod("share_slide_screenshot");
    }

    @Override
    public void goodsWidgetComplete(String widgetId) {
        throw new NotImplementedMethodException();
    }

}
