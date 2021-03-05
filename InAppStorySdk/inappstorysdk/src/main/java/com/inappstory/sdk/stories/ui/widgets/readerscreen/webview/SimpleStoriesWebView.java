package com.inappstory.sdk.stories.ui.widgets.readerscreen.webview;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;

/**
 * Created by Paperrose on 07.06.2018.
 */

public class SimpleStoriesWebView extends WebView {

    public static String injectUnselectableStyle(String html) {
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

    boolean clientIsSet = false;

    public void restartVideo() {
        //if (isVideo) {
        stopVideo();
        playVideo();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resumeVideo();
            }
        }, 200);
        // }
    }


    public void replaceHtml(String page) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("(function(){show_slide(\"" + oldEscape(page) + "\");})()", null);
        } else {
            loadUrl("javascript:(function(){show_slide(\"" + oldEscape(page) + "\");})()");
        }
    }

    private String oldEscape(String raw) {
        String escaped = raw
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\n", " ")
                .replaceAll("\r", " ");
        //.replaceAll("muted", "");
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
        // if (!isVideo) return;
        //Log.e("playVideo", storyId + " pause");
        loadUrl("javascript:(function(){story_slide_pause();})()");
    }


    public void playVideo() {
        boolean withSound = InAppStoryManager.getInstance().soundOn;
        if (withSound) {
            loadUrl("javascript:(function(){story_slide_start('{\"muted\": false}');})()");
        } else {
            loadUrl("javascript:(function(){story_slide_start('{\"muted\": true}');})()");
        }
    }

    public void stopVideo() {
        loadUrl("javascript:(function(){story_slide_stop();})()");

    }

    public void resumeVideo() {
        loadUrl("javascript:(function(){story_slide_resume();})()");
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
        if (InAppStoryManager.getInstance().soundOn) {
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
        init();
    }

    String emptyJSString = "javascript:document.body.style.setProperty(\"color\", \"black\"); ";


    public void destroyWebView() {
        final Runtime runtime = Runtime.getRuntime();
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;
        removeAllViews();
        clearHistory();
        clearCache(true);
        loadUrl("about:blank");
        manager.loadedId = -1;
        manager.loadedIndex = -1;
        onPause();
        removeAllViews();
        destroyDrawingCache();
    }

    boolean notFirstLoading = false;

    public void loadWebData(String outerLayout, String outerData) {
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        String tmpData = outerData;
        String tmpLayout = outerLayout;
        for (String key : InAppStoryManager.getInstance().getPlaceholders().keySet()) {
            tmpData = tmpData.replace(key, InAppStoryManager.getInstance().getPlaceholders().get(key));
            tmpLayout = tmpLayout.replace(key, InAppStoryManager.getInstance().getPlaceholders().get(key));
        }
        final String data = tmpData;
        final String lt = tmpLayout;
        if (!notFirstLoading) {

            Log.e("LoadHtml", "first");
            notFirstLoading = true;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String s0 = injectUnselectableStyle(lt);
                    loadDataWithBaseURL("", s0, "text/html; charset=utf-8", "UTF-8", null);
                }
            });
        } else {
            replaceHtml(data);
        }
    }

    public StoriesWebViewManager getManager() {
        return manager;
    }

    StoriesWebViewManager manager;

    private void init() {
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(getResources().getColor(R.color.black));
        if (Build.VERSION.SDK_INT >= 19) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            setLayerType(View.LAYER_TYPE_NONE, null);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            getSettings().setTextZoom(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSettings().setOffscreenPreRaster(true);
        }

        setClickable(true);
        getSettings().setJavaScriptEnabled(true);

        manager = new StoriesWebViewManager();
        manager.setStoriesWebView(this);
    }


    public void shareComplete(int id, boolean success) {
        loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    public SimpleStoriesWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleStoriesWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    float coordinate1;

    long lastTap;


    public void freezeUI() {
        touchSlider = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getParentForAccessibility().requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (InAppStoryService.getInstance().cubeAnimation) return false;
        if (!InAppStoryService.getInstance().isConnected()) return true;
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                coordinate1 = motionEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        boolean c = super.dispatchTouchEvent(motionEvent);
        return c;
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (InAppStoryService.getInstance().cubeAnimation) return false;
        boolean c = super.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return true;
            }
        }
        return c;
    }

    boolean touchSlider = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (InAppStoryService.getInstance().cubeAnimation) return false;
        boolean c = super.onInterceptTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return false;
            }

            lastTap = System.currentTimeMillis();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            touchSlider = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getParentForAccessibility().requestDisallowInterceptTouchEvent(false);
            }
        }
        return c || touchSlider;
    }


}
