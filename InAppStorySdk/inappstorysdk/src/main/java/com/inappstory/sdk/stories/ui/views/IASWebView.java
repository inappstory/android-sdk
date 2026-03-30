package com.inappstory.sdk.stories.ui.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.ui.widgets.elasticview.DraggableElasticLayout;
import com.inappstory.sdk.stories.api.models.logs.WebConsoleLog;

import java.util.UUID;

public class IASWebView extends WebView implements NestedScrollingChild {
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
        super.evaluateJavascript(script, resultCallback);
    }

    DraggableElasticLayout parent;


    DraggableElasticLayout.DraggableElasticDragCallback callback = new DraggableElasticLayout.DraggableElasticDragCallback() {
        @Override
        public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
            swiped = elasticOffsetPixels > 0f;
            Log.e("DraggableCallback", "" + swiped);
            //super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (findElasticParent(this)) {
            parent.addListener(callback);
        }
    }

    boolean findElasticParent(View current) {
        ViewParent parentView = current.getParent();
        try {
            if (parentView == null)
                return false;
            else if (parentView instanceof DraggableElasticLayout) {
                parent = (DraggableElasticLayout) parentView;
                return true;
            } else {
                return findElasticParent((View) parentView);
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (parent != null)
            parent.removeListener(callback);
        parent = null;
        super.onDetachedFromWindow();
    }

    protected void init() {
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);


        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //setBackgroundColor(getResources().getColor(R.color.black));
        setOverScrollMode(OVER_SCROLL_NEVER);
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
        resumeTimers();
    }

    public void sendWebConsoleLog(
            ConsoleMessage consoleMessage,
            String storyId,
            int contentType, // 0 - story, 1 - iam
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


    public void destroyView() {
        removeAllViews();
        clearHistory();
        clearCache(true);
        loadUrl("about:blank");
        removeAllViews();
        destroyDrawingCache();
    }

    protected String updateHead(String html, String headAddition) {
        return html.replace("<head>",
                "<head>" + headAddition);
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


    public String setDir(String html, Context context) {
        try {
            int dir = context.getResources().getConfiguration().getLayoutDirection();
            String dirString = (dir == View.LAYOUT_DIRECTION_RTL) ? "rtl" : "ltr";
            return html.replace("{{%dir}}", dirString);
        } catch (Exception e) {
            return html;
        }
    }


    private int mLastY;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedOffsetY;
    private NestedScrollingChildHelper mChildHelper;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result;
        /*if (passOverscroll) {
            result = false;
        } else
            result = super.onInterceptTouchEvent(ev);*/

        final int action = ev.getAction();
        Log.e("ScrollEvents", "WebView TouchEv onInterceptTouchEvent " + passOverscroll + " " + action);
        //passOverscroll = false;
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean returnValue = false;
        MotionEvent event = MotionEvent.obtain(ev);
        final int action = MotionEventCompat.getActionMasked(event);
        Log.e("ScrollEvents", "WebView TouchEv onTouchEvent " + passOverscroll + " " + action);
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0;
            if (parent != null)
                parent.useSwipeCallbacks = passOverscroll;
            contentNotInScrollProcess = passOverscroll;
            contentInScrollProcess(!contentNotInScrollProcess);
        }
        int eventY = (int) event.getY();
        event.offsetLocation(0, mNestedOffsetY);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int totalScrollOffset = 0;
                int deltaY = mLastY - eventY;
                // NestedPreScroll
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    totalScrollOffset += mScrollOffset[1];
                    deltaY -= mScrollConsumed[1];
                    event.offsetLocation(0, -mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }
                if (!swiped) {
                    returnValue = super.onTouchEvent(event);
                } else {
                    returnValue = false;
                }
                // NestedScroll
                if (dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
                    totalScrollOffset += mScrollOffset[1];
                    event.offsetLocation(0, mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                    mLastY -= mScrollOffset[1];
                }
                mLastY = eventY - totalScrollOffset;
                break;
            case MotionEvent.ACTION_DOWN:
                returnValue = super.onTouchEvent(event);
                mLastY = eventY;
                // start NestedScroll
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                returnValue = super.onTouchEvent(event);
                // end NestedScroll
                // passOverscroll = false;
                swiped = false;
                stopNestedScroll();
                break;
        }
        return returnValue && !swiped;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    private String consumedToString(int[] consumed) {
        String res = "";
        for (int j : consumed) {
            res += j + ",";
        }
        return res;
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,

                                        int[] offsetInWindow) {

        return passOverscroll && mChildHelper.dispatchNestedScroll(
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                offsetInWindow
        );
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return passOverscroll && mChildHelper.dispatchNestedPreScroll(
                dx,
                dy,
                consumed,
                offsetInWindow
        );
    }

    protected void contentInScrollProcess(boolean contentInScrollProcess) {}

    public boolean passOverscroll = true;
    private boolean contentNotInScrollProcess = false;

    public void passOverscroll(boolean passOverscroll) {
        this.passOverscroll = passOverscroll;
        if (parent != null)
            parent.useSwipeCallbacks &= passOverscroll;
        contentNotInScrollProcess &= passOverscroll;
        contentInScrollProcess(!contentNotInScrollProcess);
    }

    public boolean swiped = false;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        /* if (t <= 0)
            passOverscroll = true;*/
        Log.e("ScrollEvents", "WebView onScrollChanged " + " " + l
                + " " + oldl + " " + t + " " + oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public String setDir(String html, String dirString) {
        try {
            return html.replace("{{%dir}}", dirString);
        } catch (Exception e) {
            return html;
        }
    }

}
