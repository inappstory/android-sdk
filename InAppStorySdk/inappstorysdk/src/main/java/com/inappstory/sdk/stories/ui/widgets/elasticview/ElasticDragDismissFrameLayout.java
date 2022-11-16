package com.inappstory.sdk.stories.ui.widgets.elasticview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FrameLayout} which responds to nested scrolls to create drag-dismissable layouts.
 * Applies an elasticity factor to reduce movement as you approach the given dismiss distance.
 * Optionally also scales down content during drag.
 */
public class ElasticDragDismissFrameLayout extends FrameLayout {

    // configurable attribs
    private float dragDismissDistance = Float.MAX_VALUE;
    private float dragDismissFraction = -1f;
    private float dragDismissScale = 1f;
    private boolean shouldScale = false;
    private float dragElacticity = 0.8f;

    // state
    private float totalDrag;
    private float totalDisabledDrag;
    private boolean draggingDown = false;
    private boolean draggingUp = false;
    private int mLastActionEvent;

    private List<ElasticDragDismissCallback> callbacks;

    public ElasticDragDismissFrameLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ElasticDragDismissFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ElasticDragDismissFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ElasticDragDismissFrameLayout, 0, 0);

        if (a.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissDistance)) {
            dragDismissDistance = a.getDimensionPixelSize(R.styleable
                    .ElasticDragDismissFrameLayout_dragDismissDistance, 0);
        } else if (a.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissFraction)) {
            dragDismissFraction = a.getFloat(R.styleable
                    .ElasticDragDismissFrameLayout_dragDismissFraction, dragDismissFraction);
        }
        if (a.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissScale)) {
            dragDismissScale = a.getFloat(R.styleable
                    .ElasticDragDismissFrameLayout_dragDismissScale, dragDismissScale);
            shouldScale = dragDismissScale != 1f;
        }
        if (a.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragElasticity)) {
            dragElacticity = a.getFloat(R.styleable.ElasticDragDismissFrameLayout_dragElasticity,
                    dragElacticity);
        }
        a.recycle();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ElasticDragDismissFrameLayout(Context context, AttributeSet attrs,
                                         int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    public static abstract class ElasticDragDismissCallback {

        /**
         * Called for each drag event.
         *
         * @param elasticOffset       Indicating the drag offset with elasticity applied i.e. may
         *                            exceed 1.
         * @param elasticOffsetPixels The elastically scaled drag distance in pixels.
         * @param rawOffset           Value from [0, 1] indicating the raw drag offset i.e.
         *                            without elasticity applied. A value of 1 indicates that the
         *                            dismiss distance has been reached.
         * @param rawOffsetPixels     The raw distance the user has dragged
         */
        void onDrag(float elasticOffset, float elasticOffsetPixels,
                    float rawOffset, float rawOffsetPixels) {
        }

        /**
         * Called when dragging is released and has exceeded the threshold dismiss distance.
         */
        void onDragDismissed() {
        }

        void onDragDropped() {
        }

        void touchPause() {

        }

        void touchResume() {

        }

        void swipeDown() {

        }

        void swipeUp() {

        }

    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
      //  Story st = InAppStoryService.getInstance().getDownloadManager().getStoryById(InAppStoryService.getInstance().getCurrentId());
        // if (st != null && (st.disableClose || st.hasSwipeUp()))
        //     return true;
        return (nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // if we're in a drag gesture and the user reverses up the we should take those events
        Story st = InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(InAppStoryService.getInstance().getCurrentId(), type);

        if (draggingDown && dy > 0 || draggingUp && dy < 0) {
            if (st != null && (st.disableClose || st.hasSwipeUp()))
                disabledDragScale(dy);
            else {
                dragScale(dy);
                consumed[1] = dy;
            }
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        Story st = InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(InAppStoryService.getInstance().getCurrentId(), type);
        if (st != null && (st.disableClose || st.hasSwipeUp())) {
            disabledDragScale(dyUnconsumed);
        } else {
            dragScale(dyUnconsumed);
        }
    }

    boolean isPaused = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mLastActionEvent = ev.getAction();
        if (mLastActionEvent == MotionEvent.ACTION_MOVE) {
            if (!isPaused) {
                isPaused = true;
            }
            touchPause();
        } else if (mLastActionEvent == MotionEvent.ACTION_UP || mLastActionEvent == MotionEvent.ACTION_CANCEL) {
            if (isPaused) {
                isPaused = false;
            }
            touchResume();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onStopNestedScroll(View child) {
        Story st = InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(InAppStoryService.getInstance().getCurrentId(), type);
        if (totalDisabledDrag > 400) {
            swipeUpCallback();
        } else if (st != null && !st.disableClose && totalDisabledDrag < -400) {
            swipeDownCallback();
        }
        if (Math.abs(totalDrag) >= dragDismissDistance &&
                (st != null &&
                        !(st.disableClose || st.hasSwipeUp()))) {
            dispatchDismissCallback();
        } else {
            if (mLastActionEvent == MotionEvent.ACTION_DOWN) {
                setTranslationY(0f);
                setScaleX(1f);
                setScaleY(1f);
            } else {
                if (mLastActionEvent == MotionEvent.ACTION_MOVE) {
                    isPaused = false;
                    touchResume();
                }
                animate()
                        .translationY(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200L)
                        .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(getContext()))
                        .setListener(null)
                        .start();
            }
            totalDrag = 0;
            totalDisabledDrag = 0;
            draggingDown = draggingUp = false;
            dispatchDragCallback(0f, 0f, 0f, 0f);
            dispatchDropCallback();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (dragDismissFraction > 0f) {
            dragDismissDistance = h * dragDismissFraction;
        }
    }


    public void addListener(ElasticDragDismissCallback listener) {
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }
        callbacks.add(listener);
    }

    public void removeListener(ElasticDragDismissCallback listener) {
        if (callbacks != null && callbacks.size() > 0) {
            callbacks.remove(listener);
        }
    }

    private void disabledDragScale(int scroll) {
        if (scroll == 0) return;
        totalDisabledDrag += scroll;
    }

    private void dragScale(int scroll) {
        if (scroll == 0) return;

        totalDrag += scroll;

        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
        if (scroll < 0 && !draggingUp && !draggingDown) {
            draggingDown = true;
            if (shouldScale) setPivotY(getHeight());
        } else if (scroll > 0 && !draggingDown && !draggingUp) {
            draggingUp = true;
            if (shouldScale) setPivotY(0f);
        }
        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit
        float dragFraction = (float) Math.log10(1 + (Math.abs(totalDrag) / dragDismissDistance));

        // calculate the desired translation given the drag fraction
        float dragTo = dragFraction * dragDismissDistance * dragElacticity;

        if (draggingUp) {
            // as we use the absolute magnitude when calculating the drag fraction, need to
            // re-apply the drag direction
            dragTo *= -1;
        }
        setTranslationY(dragTo);

        if (shouldScale) {
            final float scale = 1 - ((1 - dragDismissScale) * dragFraction);
            setScaleX(scale);
            setScaleY(scale);
        }

        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
        if ((draggingDown && totalDrag >= 0)
                || (draggingUp && totalDrag <= 0)) {
            totalDrag = totalDisabledDrag = dragTo = dragFraction = 0;
            draggingDown = draggingUp = false;
            setTranslationY(0f);
            setScaleX(1f);
            setScaleY(1f);
        }
        dispatchDragCallback(dragFraction, dragTo,
                Math.min(1f, Math.abs(totalDrag) / dragDismissDistance), totalDrag);
    }

    private void dispatchDragCallback(float elasticOffset, float elasticOffsetPixels,
                                      float rawOffset, float rawOffsetPixels) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                if (callback != null)
                    callback.onDrag(elasticOffset, elasticOffsetPixels,
                            rawOffset, rawOffsetPixels);
            }
        }
    }

    private void dispatchDismissCallback() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                if (callback != null)
                    callback.onDragDismissed();
            }
        }
    }

    private void swipeDownCallback() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                if (callback != null)
                    callback.swipeDown();
            }
        }
    }

    public Story.StoryType type = Story.StoryType.COMMON;

    private void swipeUpCallback() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                if (callback != null)
                    callback.swipeUp();
            }
        }
    }

    private void touchPause() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                if (callback != null)
                    callback.touchPause();
            }
        }
    }

    private void touchResume() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                if (callback != null)
                    callback.touchResume();
            }
        }
    }


    private void dispatchDropCallback() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                if (callback != null)
                    callback.onDragDropped();
            }
        }
    }


    /**
     * An {@link ElasticDragDismissCallback} which fades system chrome (i.e. status bar and
     * navigation bar) whilst elastic drags are performed and
     * {@link Activity#finishAfterTransition() finishes} the activity when drag dismissed.
     */
    public static class SystemChromeFader extends ElasticDragDismissCallback {

        private final Activity activity;
        private final int statusBarAlpha;
        private final int navBarAlpha;
        private final boolean fadeNavBar;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public SystemChromeFader(Activity activity) {
            this.activity = activity;
            statusBarAlpha = Color.alpha(activity.getWindow().getStatusBarColor());
            navBarAlpha = Color.alpha(activity.getWindow().getNavigationBarColor());
            fadeNavBar = ViewUtils.isNavBarOnBottom(activity);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDrag(float elasticOffset, float elasticOffsetPixels,
                           float rawOffset, float rawOffsetPixels) {
            if (elasticOffsetPixels > 0) {
                // dragging downward, fade the status bar in proportion
                activity.getWindow().setStatusBarColor(ColorUtils.modifyAlpha(activity.getWindow()
                        .getStatusBarColor(), (int) ((1f - rawOffset) * statusBarAlpha)));
            } else if (elasticOffsetPixels == 0) {
                // reset
                activity.getWindow().setStatusBarColor(ColorUtils.modifyAlpha(
                        activity.getWindow().getStatusBarColor(), statusBarAlpha));
                activity.getWindow().setNavigationBarColor(ColorUtils.modifyAlpha(
                        activity.getWindow().getNavigationBarColor(), navBarAlpha));
            } else if (fadeNavBar) {
                // dragging upward, fade the navigation bar in proportion
                activity.getWindow().setNavigationBarColor(
                        ColorUtils.modifyAlpha(activity.getWindow().getNavigationBarColor(),
                                (int) ((1f - rawOffset) * navBarAlpha)));
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onDragDismissed() {
            activity.finishAfterTransition();
        }


        public void onDragDropped() {

        }

        @Override
        public void touchPause() {
        }

        @Override
        public void touchResume() {
        }


        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeUp() {
        }
    }

}