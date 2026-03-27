package com.inappstory.sdk.stories.ui.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
       /* if (findElasticParent(this)) {
            parent.addListener(new DraggableElasticLayout.DraggableElasticDragCallback() {
                @Override
                public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                    if (elasticOffsetPixels <= 0f) {
                        passOverscroll = false;
                    }
                    //super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                }
            });
        }*/


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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
       /* if (passOverscroll && action == MotionEvent.ACTION_MOVE) {
            MotionEvent cancelEvent = MotionEvent.obtain(ev);
            cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
            dispatchTouchEvent(cancelEvent);
        }*/
        Log.e("ScrollEvents", "WebView TouchEv dispatchTouchEvent " + passOverscroll + " " + action);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean returnValue = false;
        MotionEvent event = MotionEvent.obtain(ev);
        final int action = MotionEventCompat.getActionMasked(event);
        Log.e("ScrollEvents", "WebView TouchEv onTouchEvent " + passOverscroll + " " + action);
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0;
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
                if (!passOverscroll) {
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
                passOverscroll = false;
                stopNestedScroll();
                break;
        }
        return returnValue && !passOverscroll;
    }

    // Nested Scroll implements
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
        boolean res = mChildHelper.dispatchNestedScroll(
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                offsetInWindow
        );
        Log.e("ScrollEvents", "WebView dispatchNestedScroll " + " " + dyConsumed
                + " " + " " + dyUnconsumed + " " + consumedToString(offsetInWindow));
        return passOverscroll && res;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        boolean res = mChildHelper.dispatchNestedPreScroll(
                dx,
                dy,
                consumed,
                offsetInWindow
        );
        Log.e("ScrollEvents", "WebView dispatchNestedPreScroll " + " " + mNestedOffsetY + " " + mLastY + " " + dy + " " +
                consumedToString(consumed) + " " + consumedToString(offsetInWindow) + " " + res);

        return passOverscroll && res;
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return passOverscroll && mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    private boolean passOverscroll = false;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t <= 0)
            passOverscroll = true;
        Log.e("ScrollEvents", "WebView onScrollChanged " + " " + l
                + " " + oldl + " " + t + " " + oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return passOverscroll && mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public String setDir(String html, String dirString) {
        try {
            return html.replace("{{%dir}}", dirString);
        } catch (Exception e) {
            return html;
        }
    }

}
