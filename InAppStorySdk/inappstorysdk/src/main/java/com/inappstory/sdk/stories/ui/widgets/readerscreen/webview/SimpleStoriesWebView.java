package com.inappstory.sdk.stories.ui.widgets.readerscreen.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.stories.ui.views.StoriesReaderWebViewClient;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.SimpleStoriesView;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by Paperrose on 07.06.2018.
 */

public class SimpleStoriesWebView extends IASWebView implements SimpleStoriesView {

    private

    boolean clientIsSet = false;

    public void restartVideo() {
        stopVideo();
        playVideo();
    }

    private void logMethod(String payload) {
        InAppStoryManager.showDLog("JS_method_call", manager.storyId + " " + payload);
    }

    public void gameComplete(String data) {
        if (data != null)
            loadUrl("javascript:game_complete('" + data + "')");
        else
            loadUrl("javascript:game_complete()");
        logMethod("game_complete " + data);
    }

    private void replaceHtml(String page) {
        evaluateJavascript("(function(){show_slide(\"" + oldEscape(page) + "\");})()", null);

        logMethod("show_slide");
    }

    private String oldEscape(String raw) {
        String escaped = raw
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\n", " ")
                .replaceAll("\r", " ");
        return escaped;
    }

    private String escape(String raw) {
        String escaped = raw;
        escaped = escaped.replaceAll("\"", "\\\"");
        escaped = escaped.replaceAll("\b", "\\b");
        escaped = escaped.replaceAll("\f", "\\f");
        escaped = escaped.replaceAll("\n", "\\n");
        escaped = escaped.replaceAll("\r", "\\r");
        escaped = escaped.replaceAll("\t", "\\t");
        // TODO: escape other non-printing characters using uXXXX notation
        return escaped;
    }

    public void pauseVideo() {
        loadUrl("javascript:(function(){story_slide_pause();})()");

        logMethod("story_slide_pause");
    }


    public void playVideo() {
        if (InAppStoryService.getInstance().isSoundOn()) {
            loadUrl("javascript:(function(){story_slide_start('{\"muted\": false}');})()");
        } else {
            loadUrl("javascript:(function(){story_slide_start('{\"muted\": true}');})()");
        }
        logMethod("story_slide_start");
    }

    public void stopVideo() {
        loadUrl("javascript:(function(){story_slide_stop();})()");

        logMethod("story_slide_stop");
    }

    @Override
    public void swipeUp() {

        loadUrl("javascript:window.story_slide_swipe_up()");
        logMethod("story_slide_swipe_up");
    }

    @Override
    public void loadJsApiResponse(String result, String cb) {
        evaluateJavascript(cb + "('" + result + "');", null);
    }

    public void resumeVideo() {
        loadUrl("javascript:(function(){story_slide_resume();})()");
        logMethod("story_slide_resume");
    }

    @Override
    public void loadUrl(final String url) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SimpleStoriesWebView.super.loadUrl(url);
            }
        });
    }

    public void changeSoundStatus() {
        if (InAppStoryService.getInstance().isSoundOn()) {
            loadUrl("javascript:(function(){story_slide_enable_audio();})()");
        } else {
            loadUrl("javascript:(function(){story_slide_disable_audio();})()");
        }
    }

    public void cancelDialog(String id) {
        loadUrl("javascript:(function(){story_send_text_input_result(\"" + id + "\", \"\");})()");
    }

    public void sendDialog(String id, String data) {
        data = data.replaceAll("\n", "<br>");
        loadUrl("javascript:story_send_text_input_result(\"" + id + "\", \"" + data + "\")");
    }

    public SimpleStoriesWebView(Context context) {
        super(context);
    }

    //  String emptyJSString = "javascript:document.body.style.setProperty(\"color\", \"black\"); ";


    public void destroyView() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;
        removeAllViews();
        clearHistory();
        clearCache(true);
        loadUrl("about:blank");
        manager.loadedId = -1;
        manager.loadedIndex = -1;
        removeAllViews();
        destroyDrawingCache();
    }

    @Override
    public float getCoordinate() {
        return coordinate1;
    }

    boolean notFirstLoading = false;

    public void loadWebData(String outerLayout, String outerData) {
        String tmpData = outerData;
        String tmpLayout = outerLayout;

        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            Map<String, String> localPlaceholders = service.getPlaceholders();
            for (String key : localPlaceholders.keySet()) {
                String modifiedKey = "%" + key + "%";
                String value = localPlaceholders.get(key);
                if (value != null) {
                    tmpData = tmpData.replace(modifiedKey, value);
                    tmpLayout = tmpLayout.replace(modifiedKey, value);
                }
            }
        }
        final String data = tmpData;
        final String lt = tmpLayout;

        if (!notFirstLoading) {
            notFirstLoading = true;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String s0 = setDir(injectUnselectableStyle(lt));
                    loadDataWithBaseURL("file:///data/", s0, "text/html; charset=utf-8", "UTF-8", null);
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    replaceHtml(data);
                }
            });
        }
    }

    public StoriesViewManager getManager() {
        return manager;
    }

    StoriesViewManager manager;

    protected void init() {
        super.init();
        manager = new StoriesViewManager(getContext());
        manager.setStoriesView(this);
    }


    public void shareComplete(String id, boolean success) {
        if (id == null) return;
        loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
        logMethod("share_complete " + id + " " + success);
    }

    public SimpleStoriesWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleStoriesWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float coordinate1;

    long lastTap;


    public void freezeUI() {
        touchSlider = true;

        getParentForAccessibility().requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void screenshotShare() {
        evaluateJavascript("share_slide_screenshot();", null);
        logMethod("share_slide_screenshot");
    }

    @Override
    public void setStoriesView(SimpleStoriesView storiesView) {

    }

    @Override
    public void checkIfClientIsSet() {

        if (!clientIsSet) {
            addJavascriptInterface(new WebAppInterface(getContext(),
                    getManager()), "Android");

            setWebViewClient(new StoriesReaderWebViewClient(getManager()));

            setWebChromeClient(new WebChromeClient() {
                @Nullable
                @Override
                public Bitmap getDefaultVideoPoster() {
                    if (super.getDefaultVideoPoster() == null) {
                        Bitmap bmp = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bmp);
                        canvas.drawColor(Color.BLACK);
                        return bmp;
                    } else {
                        return super.getDefaultVideoPoster();
                    }
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (getManager().getProgressBar() != null)
                        getManager().getProgressBar().setProgress(newProgress);

                }

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

                    if (manager != null) {
                        sendWebConsoleLog(consoleMessage,
                                Integer.toString(manager.storyId),
                                manager.index);
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
    public void goodsWidgetComplete(String widgetId) {
        evaluateJavascript("goods_widget_complete(\"" + widgetId + "\");", null);

        logMethod("goods_widget_complete " + widgetId);
    }

    private boolean checkIfParentsHasCubeAnimation(ViewParent view) {
        if (view == null) return false;
        if (view instanceof ReaderPager) {
            return ((ReaderPager) view).cubeAnimation;
        }
        return checkIfParentsHasCubeAnimation(view.getParentForAccessibility());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (checkIfParentsHasCubeAnimation(getParentForAccessibility())) return false;
        //if (!InAppStoryService.isConnected()) return true;

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                coordinate1 = motionEvent.getX();
                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    int sz = (!Sizes.isTablet() ? Sizes.getScreenSize().x : Sizes.dpToPxExt(400));
                    coordinate1 = sz - coordinate1;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        boolean c = super.dispatchTouchEvent(motionEvent);
        return c;
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (checkIfParentsHasCubeAnimation(getParentForAccessibility())) return false;
        boolean c = super.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return true;
            }
        } else if (Sizes.isTablet() && motionEvent.getAction() == MotionEvent.ACTION_UP) {
            getManager().getPageManager().resumeSlide(false);
        }
        return c;
    }

    boolean touchSlider = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (checkIfParentsHasCubeAnimation(getParentForAccessibility())) return false;
        boolean c = super.onInterceptTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return false;
            }
            getManager().getPageManager().pauseSlide(false);

            lastTap = System.currentTimeMillis();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            touchSlider = false;

            getParentForAccessibility().requestDisallowInterceptTouchEvent(false);
        }
        return c || touchSlider;
    }


}
