package com.inappstory.sdk.stories.ui.reader.views.storiesdisplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.StoryReaderWebViewClient;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoryDisplay;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.DisableTouchEvent;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.IStoriesDisplayViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.IStoriesWebViewDisplayViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.SlideContentState;
import com.inappstory.sdk.stories.utils.Sizes;

/**
 * Created by Paperrose on 07.06.2018.
 */

public class StoriesWebViewDisplay extends IASWebView {

    IStoriesWebViewDisplayViewModel viewModel;

    public void setViewModel(IStoriesDisplayViewModel viewModel) {
        if (viewModel instanceof IStoriesWebViewDisplayViewModel) {
            this.viewModel = (IStoriesWebViewDisplayViewModel) viewModel;
            addJavascriptInterface(new WebAppInterface(viewModel), "Android");
            if (isAttachedToWindow()) observeStates();
        }
    }


    private void observeStates() {
        viewModel.loadUrlCalls().observeForever(loadUrlObserver);
        viewModel.evaluateJSCalls().observeForever(evaluateJSObserver);
        viewModel.slideContentState().observeForever(slideContentObserver);
    }

    private void removeObservers() {
        viewModel.loadUrlCalls().removeObserver(loadUrlObserver);
        viewModel.evaluateJSCalls().removeObserver(evaluateJSObserver);
        viewModel.slideContentState().removeObserver(slideContentObserver);
    }


    Observer<String> loadUrlObserver = new Observer<String>() {
        @Override
        public void onChanged(@NonNull String text) {
            loadUrl(text);
        }
    };

    Observer<String> evaluateJSObserver = new Observer<String>() {
        @Override
        public void onChanged(@NonNull String text) {
            evaluateJavascript(text, null);
        }
    };

    Observer<SlideContentState> slideContentObserver = new Observer<SlideContentState>() {
        @Override
        public void onChanged(@NonNull SlideContentState state) {
            if (viewModel.getStoryDisplayState().firstLoading() || state.getPage().isEmpty()) {
                viewModel.setStateAsLoaded();
                String s0 = setDir(injectUnselectableStyle(state.getLayout()));
                loadDataWithBaseURL("file:///data/",
                        s0,
                        "text/html; charset=utf-8",
                        "UTF-8",
                        null
                );
            } else {
                replaceHtml(state.getPage());
            }
        }
    };

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

    public StoriesWebViewDisplay(Context context) {
        super(context);
    }

    public void destroyView() {
        removeAllViews();
        clearHistory();
        clearCache(true);
        loadUrl("about:blank");
        removeAllViews();
        destroyDrawingCache();
    }

    public float getCoordinate() {
        return coordinate1;
    }

    private void replaceHtml(String page) {
        evaluateJavascript("(function(){show_slide(\"" + oldEscape(page) + "\");})()", null);
    }


    protected void init() {
        super.init();
        setWebViewClient(new StoryReaderWebViewClient());
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

            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (viewModel != null)
                    sendWebConsoleLog(
                            consoleMessage,
                            Integer.toString(viewModel.getStoryDisplayState().storyId()),
                            viewModel.getStoryDisplayState().slideIndex()
                    );
                Log.d("InAppStory_SDK_Web", consoleMessage.messageLevel().name() + ": "
                        + consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (viewModel != null) {
            observeStates();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (viewModel != null) {
            removeObservers();
        }
    }

    public StoriesWebViewDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StoriesWebViewDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float coordinate1;

    long lastTap;


    public void disableTouchEvent(DisableTouchEvent disableDispatchTouchEvent) {
        this.disableTouchEvent = disableDispatchTouchEvent;
    }

    private DisableTouchEvent disableTouchEvent;

    private boolean touchIsDisabled() {
        return disableTouchEvent != null && disableTouchEvent.isDisabled();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (touchIsDisabled())
            return false;
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                coordinate1 = motionEvent.getX();
                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    int sz = (!Sizes.isTablet(getContext()) ? Sizes.getScreenSize(getContext()).x
                            : Sizes.dpToPxExt(400, getContext()));
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
        if (touchIsDisabled())
            return false;
        boolean c = super.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return true;
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (Sizes.isTablet(getContext()))
                viewModel.resumeSlide();
        }
        return c;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (touchIsDisabled())
            return false;
        boolean c = super.onInterceptTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return false;
            }
            if (viewModel != null)
                viewModel.pauseSlide();
            lastTap = System.currentTimeMillis();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

            if (viewModel != null)
                viewModel.unfreezeUI();
        }
        return c || (viewModel != null && viewModel.isUIFrozen());
    }


}
