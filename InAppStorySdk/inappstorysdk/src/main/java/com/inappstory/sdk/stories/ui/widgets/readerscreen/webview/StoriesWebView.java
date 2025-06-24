package com.inappstory.sdk.stories.ui.widgets.readerscreen.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderSlideViewModel;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ContentViewInteractor;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.StringsUtils;

/**
 * Created by Paperrose on 07.06.2018.
 */

public class StoriesWebView extends IASWebView implements ContentViewInteractor {

    private boolean clientIsSet = false;

    public void restartSlide(IASCore core) {
        boolean isSoundOn = ((IASDataSettingsHolder) core.settingsAPI()).isSoundOn();
        String funAfterCheck =
                isSoundOn ?
                        "story_slide_restart('{\"muted\": false}');" :
                        "story_slide_restart('{\"muted\": true}');";
        loadUrl("javascript:(function(){" +
                "if ('story_slide_restart' in window) " +
                "{" +
                " window." + funAfterCheck +
                "}" +
                "})()");
        logMethod("story_slide_restart");
    }

    private void logMethod(String payload) {
        InAppStoryManager.showDLog("JS_method_call",
                manager.storyId + " " + manager.loadedIndex + " " + payload);
    }

    public void handleBackPress() {
        evaluateJavascript("handleBackpress();", null);
    }

    public void gameComplete(String data) {
        if (data != null)
            loadUrl("javascript:game_complete('" + StringsUtils.escapeSingleQuotes(data) + "')");
        else
            loadUrl("javascript:game_complete()");
        logMethod("game_complete " + data);
    }

    String currentPage = "";


    @Override
    public void clearSlide(int index) {
        if (index < 0) return;
        evaluateJavascript("(function(){clear_slide(" + index + ");})()", null);
        logMethod("clear_slide " + index);
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


    @Override
    public void loadSlide(String content) {
        loadDataWithBaseURL(
                "file:///data/",
                content,
                "text/html; charset=utf-8",
                "UTF-8",
                null
        );
    }

    @Override
    public void replaceSlide(String newContent) {
        evaluateJavascript("(function(){show_slide(\"" + newContent + "\");})()", null);
        logMethod("show_slide");
    }

    public void pauseSlide() {
        loadUrl("javascript:(function(){" +
                "if ('story_slide_pause' in window) " +
                "{" +
                " window.story_slide_pause(); " +
                "}" +
                "})()");

        logMethod("story_slide_pause");
    }

    public void startSlide(IASCore core) {
        boolean isSoundOn = ((IASDataSettingsHolder) core.settingsAPI()).isSoundOn();
        String funAfterCheck =
                isSoundOn ?
                        "story_slide_start('{\"muted\": false}');" :
                        "story_slide_start('{\"muted\": true}');";
        loadUrl("javascript:(function(){" +
                "if ('story_slide_start' in window) " +
                "{" +
                " window." + funAfterCheck +
                "}" +
                "})()");
        logMethod("story_slide_start");
    }

    public void autoSlideEnd() {
        if (manager.loadedIndex < 0) return;
        loadUrl("javascript:(function(){" +
                "if ('story_slide_timer_end' in window) " +
                "{" +
                " window.story_slide_timer_end(); " +
                "}" +
                "})()");

    }

    public void stopSlide(boolean newPage) {
        String funAfterCheck = newPage ?
                "story_slide_stop('{\"prepareForRestart\": true}'); " :
                "story_slide_stop('{\"prepareForRestart\": false}'); ";
        if (manager.loadedIndex < 0) return;

        Log.e("stopSlide", funAfterCheck);
        loadUrl("javascript:(function(){" +
                "if ('story_slide_stop' in window) " +
                "{" +
                " window." + funAfterCheck +
                "}" +
                "})()");

        logMethod("story_slide_stop");
    }

    @Override
    public void swipeUp() {

        loadUrl("javascript:window.story_slide_swipe_up()");
        logMethod("story_slide_swipe_up");
    }

    @Override
    public void loadJsApiResponse(String result, String cb) {
        evaluateJavascript(cb + "('" + StringsUtils.escapeSingleQuotes(result) + "');", null);
    }

    public void resumeSlide() {
        loadUrl("javascript:(function() {" +
                "if ('story_slide_resume' in window) " +
                "{" +
                " window.story_slide_resume(); " +
                "}" +
                "})()");
        /*  postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUrl("javascript:(function() {window._updateTimeline(0,\"start\",0, 10000, false, false);})()");
            }
        },5000);*/
        logMethod("story_slide_resume");
    }

    @Override
    public Context getActivityContext() {
        return context;
    }

    @Override
    public void loadUrl(final String url) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                StoriesWebView.super.loadUrl(url);
            }
        });
    }

    public void changeSoundStatus(IASCore core) {
        if (((IASDataSettingsHolder) core.settingsAPI()).isSoundOn()) {
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


    public StoriesWebView(final Context context) {
        super(context.getApplicationContext());
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                StoriesWebView.this.context = context;
                manager = new StoriesViewManager(context, core);
                manager.setStoriesView(StoriesWebView.this);
            }
        });
    }


    private Context context;

    //  String emptyJSString = "javascript:document.body.style.setProperty(\"color\", \"black\"); ";


    public void destroyView() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;
        currentPage = "";
        removeAllViews();
        clearHistory();
        clearCache(true);
        loadUrl("about:blank");
        manager.loadedId = -1;
        manager.loadedIndex = -1;
        removeAllViews();
        destroyDrawingCache();
        this.context = null;
    }

    @Override
    public float getCoordinate() {
        return coordinate1;
    }

    boolean notFirstLoading = false;

    public void loadWebData(String firstData, final String replaceData) {
        currentPage = replaceData;
        if (!notFirstLoading || replaceData.isEmpty()) {
            notFirstLoading = true;
            final String modifiedPageAndLayout = setDir(injectUnselectableStyle(firstData),
                    context != null ? context : getContext()
            );
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadSlide(modifiedPageAndLayout);
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    replaceSlide(oldEscape(replaceData));
                }
            });
        }
    }

    public StoriesViewManager getManager() {
        return manager;
    }

    StoriesViewManager manager;


    public void shareComplete(String id, boolean success) {
        if (id == null) return;
        loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
        logMethod("share_complete " + id + " " + success);
    }

    public StoriesWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StoriesWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float coordinate1;

    long lastTap;


    public void freezeUI() {
        touchSlider = true;

        getParentForAccessibility().requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void unfreezeUI() {
        touchSlider = false;
        getParentForAccessibility().requestDisallowInterceptTouchEvent(false);
        getManager().getPageManager().resumeSlide(false);
    }


    @Override
    public void screenshotShare(String shareId) {
        evaluateJavascript("share_slide_screenshot(\"" + shareId + "\");", null);
        logMethod("share_slide_screenshot");
    }

    @Override
    public void checkIfClientIsSet() {

        if (!clientIsSet) {
            addJavascriptInterface(
                    new WebAppInterface(
                            getManager(),
                            getManager().core()
                    ), "Android");
            setWebViewClient(new IASWebViewClient());
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
                   /* if (getManager().getProgressBar() != null)
                        getManager().getProgressBar().setProgress(newProgress, 100);
*/
                }

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

                    if (manager != null) {
                        sendWebConsoleLog(consoleMessage,
                                Integer.toString(manager.storyId),
                                0,
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

    @Override
    public void slideViewModel(IIAMReaderSlideViewModel slideViewModel) {

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

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                coordinate1 = motionEvent.getX();
                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    int sz = (!Sizes.isTablet(getContext()) ?
                            Sizes.getScreenSize(getContext()).x :
                            Sizes.dpToPxExt(400, getContext()));
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
        Log.e("ViewPagerTouch", "WebView onTouchEvent " + motionEvent);
        if (checkIfParentsHasCubeAnimation(getParentForAccessibility())) return false;
        boolean c = super.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return true;
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (Sizes.isTablet(getContext()))
                getManager().getPageManager().resumeSlide(false);
        }
        return c;
    }

    boolean touchSlider = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        Log.e("ViewPagerTouch", "WebView onInterceptTouchEvent " + motionEvent);
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
