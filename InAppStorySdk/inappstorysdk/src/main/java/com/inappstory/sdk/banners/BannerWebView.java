package com.inappstory.sdk.banners;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.banners.BannerJavascriptInterface;
import com.inappstory.sdk.core.banners.BannerViewModel;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.inappmessage.ui.reader.IAMReaderJavascriptInterface;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ContentViewInteractor;
import com.inappstory.sdk.stories.utils.Sizes;

public class BannerWebView extends IASWebView implements ContentViewInteractor {
    private boolean clientIsSet = false;

    public void setHost(BannerView host) {
        this.host = host;
    }

    private BannerView host;

    public BannerWebView(@NonNull Context context) {
        super(context);
    }

    public BannerWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    IBannerViewModel slideViewModel;

    private void logMethod(String payload) {
        if (slideViewModel == null) return;
        int contentIdWithIndex = slideViewModel.contentIdAndType().contentId;
        InAppStoryManager.showDLog("JS_method_call",
                contentIdWithIndex + " " + 0 + " " + payload);
    }


    boolean touchSlider = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean c = super.onInterceptTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (host != null) host.pauseBanner();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            touchSlider = false;
            getParentForAccessibility().requestDisallowInterceptTouchEvent(false);
        }
        return c || touchSlider;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean c = super.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (host != null) host.resumeBanner();
        }
        return c;
    }

    @Override
    public void loadSlide(String content) {
        if (slideViewModel == null) return;
        String newContent = setDir(content);
        loadDataWithBaseURL(
                "file:///data/",
                newContent,
                "text/html; charset=utf-8",
                "UTF-8",
                null
        );
    }

    @Override
    public void replaceSlide(String newContent) {

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
    public void stopSlide() {
        loadUrl("javascript:(function(){" +
                "if ('story_slide_stop' in window) " +
                "{" +
                " window.story_slide_stop(); " +
                "}" +
                "})()");

        logMethod("story_slide_stop");
    }

    @Override
    public void swipeUp() {

    }

    @Override
    public void clearSlide(int index) {
        if (index < 0) return;
        evaluateJavascript("(function(){clear_slide(" + index + ");})()", null);
        logMethod("clear_slide " + index);
    }

    @Override
    public void loadJsApiResponse(String result, String cb) {
        evaluateJavascript(cb + "('" + result + "');", null);
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

    }

    @Override
    public void sendDialog(String id, String data) {

    }

    @Override
    public float getCoordinate() {
        return 0;
    }

    @Override
    public void shareComplete(String stId, boolean success) {

    }

    @Override
    public void freezeUI() {

    }

    @Override
    public void unfreezeUI() {

    }

    @Override
    public void checkIfClientIsSet() {
        if (!clientIsSet) {
            if (slideViewModel == null) return;
            addJavascriptInterface(
                    new BannerJavascriptInterface(slideViewModel),
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
                    int id = slideViewModel.contentIdAndType().contentId;
                    sendWebConsoleLog(
                            consoleMessage,
                            Integer.toString(id),
                            2,
                            0
                    );

                    Log.d("InAppStory_SDK_Banners", consoleMessage.messageLevel().name() + ": "
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

    }

    @Override
    public void goodsWidgetComplete(String widgetId) {

    }

    @Override
    public void slideViewModel(IReaderSlideViewModel slideViewModel) {
        this.slideViewModel = (IBannerViewModel) slideViewModel;
    }
}
