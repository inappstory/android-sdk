package com.inappstory.sdk.core.ui.widgets.bottomsheet;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;

public class BottomSheetLayout extends RoundedCornerLayout {
    private ValueAnimator valueAnimator = null;
    private int collapsedHeight = 0;
    private float progress = 0f;
    private boolean startsCollapsed = true;
    private float scrollTranslationY = 0f;
    private float userTranslationY = 0f;
    private boolean isScrollingUp = false;
    private BottomSheetProgressListener bottomSheetProgressListener = null;
    private final TouchToDragListener touchToDragListener =
            new TouchToDragListener(true);

    public void setProgressListener(
            BottomSheetProgressListener bottomSheetProgressListener
    ) {
        this.bottomSheetProgressListener = bottomSheetProgressListener;
    }

    boolean isExpand() {
        return progress == 1f;
    }

    @Override
    public void setTranslationY(float translationY) {
        userTranslationY = translationY;
        super.setTranslationY(scrollTranslationY + userTranslationY);
    }


    public void setCollapsedHeight(int height) {
        collapsedHeight = height;
        setMinimumHeight(Math.max(getMinimumHeight(), collapsedHeight));
    }

    public void toggle() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        long duration;
        if (progress > 0.5f) {
            duration = (long) (animationDuration * progress);
            valueAnimator = ValueAnimator.ofFloat(progress, 0f);
        } else {
            duration = (long) (animationDuration * (1 - progress));
            valueAnimator = ValueAnimator.ofFloat(progress, 1f);
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                animate(progress);
            }
        });

        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void collapse() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofFloat(progress, 0f);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                animate(progress);
            }
        });

        valueAnimator.setDuration((long) (animationDuration * progress));

        valueAnimator.start();
    }

    public void expand() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofFloat(progress, 1f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                animate(progress);
            }
        });

        valueAnimator.setDuration((long) (animationDuration * (1 - progress)));
        valueAnimator.start();
    }

    private void animate(float progress) {
        this.progress = progress;
        float height = getHeight();
        float distance = height - collapsedHeight;
        scrollTranslationY = distance * (1 - progress);
        super.setTranslationY(scrollTranslationY + userTranslationY);
        if (bottomSheetProgressListener != null) {
            bottomSheetProgressListener.onProgress(progress);
        }
    }

    private void animateScroll(float firstPos, float touchPos) {
        float distance = touchPos - firstPos;
        float height = getHeight();
        float totalDistance = height - collapsedHeight;
        float progress = this.progress;
        if (!startsCollapsed) {
            isScrollingUp = false;
            progress = Math.max(0f, 1 - distance / totalDistance);
        } else if (startsCollapsed) {
            isScrollingUp = true;
            progress = Math.min(1f, -distance / totalDistance);
        }
        progress = Math.max(0f, Math.min(1f, progress));
        animate(progress);
    }

    private void animateScrollEnd() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        long duration;
        float progressLimit = isScrollingUp ? 0.2f : 0.8f;
        if (progress > progressLimit) {
            duration = (long) (animationDuration * (1 - progress));
            valueAnimator = ValueAnimator.ofFloat(progress, 1f);
        } else {
            duration = (long) (animationDuration * progress);
            valueAnimator = ValueAnimator.ofFloat(progress, 0f);
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                animate(progress);
            }
        });

        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }


    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private OnClickListener clickListener = null;
    private long animationDuration = 300;

    public BottomSheetLayout(@NonNull Context context) {
        super(context);
        initView(null);
    }

    public BottomSheetLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
        initView(attrs);
    }

    public BottomSheetLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BottomSheetLayout);
        int collapsedHeight = a.getDimensionPixelSize(R.styleable.BottomSheetLayout_collapsedHeight, 0);

        setCollapsedHeight(collapsedHeight);
        setMinimumHeight(Math.max(getMinimumHeight(), collapsedHeight));
        a.recycle();

        valueAnimator = ValueAnimator.ofFloat(0f, 1f);

        setOnTouchListener(touchToDragListener);

        if (getHeight() == 0) {
            addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(
                        View v,
                        int left,
                        int top,
                        int right,
                        int bottom,
                        int oldLeft,
                        int oldTop,
                        int oldRight,
                        int oldBottom
                ) {
                    removeOnLayoutChangeListener(this);
                    animate(0f);
                }
            });
        } else {
            animate(0f);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev != null) {
            return touchToDragListener.onTouch(this, ev);
        }
        return false;
    }

    private boolean performChildClick(float eventX, float eventY) {
        return performChildClick(eventX, eventY, this, 0);
    }

    private boolean performChildClick(float eventX, float eventY, ViewGroup viewGroup, int nest) {
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View view = viewGroup.getChildAt(i);
            if (isViewAtLocation(eventX, eventY, view)) {
                if (view instanceof ViewGroup) {
                    boolean performChildClick = performChildClick(eventX - view.getLeft(), eventY - view.getTop(), (ViewGroup) view, nest + 1);
                    if (performChildClick) {
                        return true;
                    }
                }
                if (view.performClick()) {
                    return true;
                }
            }
        }
        return performClick();
    }

    private boolean isViewAtLocation(float rawX, float rawY, View view) {
        if (view.getLeft() <= rawX && view.getRight() >= rawX) {
            if (view.getTop() <= rawY && view.getBottom() >= rawY) {
                return true;
            }
        }
        return false;
    }

    private void onClick() {
        if (clickListener != null) {
            clickListener.onClick(this);
        }
    }

    class TouchToDragListener implements View.OnTouchListener {

        private static final int CLICK_ACTION_THRESHOLD = 200;
        private float startX;
        private float startY;
        private double startTime;
        private final boolean touchToDrag;

        public TouchToDragListener(boolean touchToDrag) {
            this.touchToDrag = touchToDrag;
        }

        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            int action = ev.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (ev.getPointerCount() == 1) {
                        startX = ev.getRawX();
                        startY = ev.getRawY();
                        startTime = System.currentTimeMillis();
                        startsCollapsed = progress < 0.3;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    float y = ev.getRawY();
                    animateScroll(startY, y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    float endX = ev.getRawX();
                    float endY = ev.getRawY();
                    if (isAClick(startX, endX, startY, endY, System.currentTimeMillis())) {
                        if (performChildClick(ev.getX(), ev.getY())) {
                            return true;
                        }
                        if (touchToDrag && clickListener != null) {
                            onClick(); // WE HAVE A CLICK!!
                            return true;
                        }
                    }
                    animateScrollEnd();
                    break;
            }
            return true;
        }

        private boolean isAClick(float startX, float endX, float startY, float endY, long endTime) {
            float differenceX = Math.abs(startX - endX);
            float differenceY = Math.abs(startY - endY);
            long differenceTime = Math.abs((long) startTime - endTime);
            return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD || differenceTime > 400);
        }
    }
}
