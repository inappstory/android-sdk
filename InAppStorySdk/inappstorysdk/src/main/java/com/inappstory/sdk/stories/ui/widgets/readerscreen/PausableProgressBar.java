package com.inappstory.sdk.stories.ui.widgets.readerscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;

final class PausableProgressBar extends FrameLayout {

    /***
     * progress
     */
    private static final int DEFAULT_PROGRESS_DURATION = 2000;

    private View frontProgressView;
    private View maxProgressView;

    private PausableScaleAnimation animation;
    public long duration = -7;
    private Callback callback;

    interface Callback {
        void onStartProgress();

        boolean onFinishProgress();

        boolean onFinishProgress(boolean isDestroy);
    }

    public boolean animationIsEmpty() {
        return animation == null;
    }

    public PausableProgressBar(Context context) {
        this(context, null);
    }

    public PausableProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PausableProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.cs_pausable_progress, this);
        frontProgressView = findViewById(R.id.front_progress);
        maxProgressView = findViewById(R.id.max_progress); // work around
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCallback(@NonNull Callback callback) {
        this.callback = callback;
    }

    void setMax() {
        finishProgress(true);
    }

    void setMaxWithoutCallback() {
        maxProgressView.setBackgroundResource(R.color.cs_progressMaxActive);
        maxProgressView.setVisibility(VISIBLE);
    }

    void setMin() {
        finishProgress(false);
    }

    void setMinWithoutCallback() {
        maxProgressView.setBackgroundResource(R.color.cs_progressSecondary);

        maxProgressView.setVisibility(VISIBLE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
        }
    }


    public void finishAnimationProgress() {
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
        }
    }

    public void clearProgress(boolean isDestroy) {

        maxProgressView.setVisibility(GONE);
        frontProgressView.setVisibility(INVISIBLE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
            if (callback != null) {
                callback.onFinishProgress(isDestroy);
            }
        }
    }

    public void clearProgress() {

        clearProgress(false);
    }

    private void finishProgress(boolean isMax) {
        if (isMax) maxProgressView.setBackgroundResource(R.color.cs_progressMaxActive);
        maxProgressView.setVisibility(isMax ? VISIBLE : GONE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();

        }
        if (callback != null) {
            callback.onFinishProgress();
        }
    }

    public boolean startProgress() {
        if (!active) return false;
        maxProgressView.setVisibility(GONE);

        animation = new PausableScaleAnimation(0, 1, 1, 1, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0);
        if (duration <= 1) {
            animation.setDuration(1000);
        } else {
            animation.setDuration(duration);
        }
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                frontProgressView.setVisibility(View.VISIBLE);
                if (callback != null) callback.onStartProgress();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
               /* if (callback != null) {
                    Log.e("loadStory", "onAnimationEnd");
                    if (callback.onFinishProgress()) {
                     //   EventBus.getDefault().post(new NextStoryReaderEvent());
                    } else {
                     //   EventBus.getDefault().post(new StoriesNextPageEvent(0));
                    }
                }*/
            }
        });
        animation.setFillAfter(true);
        if (duration > 1) {
            frontProgressView.startAnimation(animation);
        } else {
            //setMaxWithoutCallback();
            maxProgressView.setVisibility(GONE);
            if (animation != null) {
                animation.setAnimationListener(null);
                animation.cancel();
            }
        }

        return true;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean active = false;

    public void pauseProgress(boolean withBackground) {
        if (animation != null) {
            if (withBackground) {
                animation.pauseWithBackground();
            } else {
                animation.pause();
            }
        }
    }

    public void resumeProgress(boolean withBackground) {
        if (!active) return;
        if (animation != null) {
            if (withBackground) {
                animation.resumeWithBackground();
            } else {
                animation.resume();
            }
        }
    }

    void clear() {
        clearProgress(true);
        if (animation != null) {
            animation = null;
        }
    }

    private class PausableScaleAnimation extends ScaleAnimation {

        private long mElapsedAtPause = 0;
        private long mSystemElapsedAtPause = 0;
        private boolean mPaused = false;
        private boolean mPausedSystem = false;
        private boolean mResumed = false;

        PausableScaleAnimation(float fromX, float toX, float fromY,
                               float toY, int pivotXType, float pivotXValue, int pivotYType,
                               float pivotYValue) {
            super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
                    pivotYValue);
        }

        private long resumedSystemTime;
        private long pausedSystemTime;
        private long totalPausedSystemTime = 0;
        private boolean pausedInBackground = false;

        @Override
        public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
            if (mResumed) {
                pausedInBackground = false;
                setStartTime(currentTime - mSystemElapsedAtPause);
                mSystemElapsedAtPause = 0;
                mElapsedAtPause = 0;
                pausedSystemTime = 0;
                resumedSystemTime = 0;
            }
            mResumed = false;
            if (mPaused && mElapsedAtPause == 0) {
                mElapsedAtPause = currentTime - getStartTime();
            }
            if (mPaused) {
                setStartTime(currentTime - mElapsedAtPause);
            }
            if (mPausedSystem && mSystemElapsedAtPause == 0) {
                mSystemElapsedAtPause = currentTime - getStartTime();
            }
            if (mPausedSystem) {
                setStartTime(currentTime - mSystemElapsedAtPause);
            }

            return super.getTransformation(currentTime, outTransformation, scale);
        }

        /***
         * pause animation
         */
        void pause() {
            if (mPaused) return;
            // mResumed = false;
            mElapsedAtPause = 0;
            mPaused = true;
        }

        /***
         * resume animation
         */
        void resume() {
            mPaused = false;
            pausedInBackground = false;
            mPausedSystem = false;
            //   resumedSystemTime = System.currentTimeMillis();
        }

        void pauseWithBackground() {
            if (pausedInBackground) return;
            pausedInBackground = true;
            mPaused = false;
            // mResumed = false;
            // pausedSystemTime = System.currentTimeMillis();
            //  mElapsedAtPause = 0;
            mSystemElapsedAtPause = 0;
            mPausedSystem = true;
        }

        void resumeWithBackground() {
            mPaused = false;
            mResumed = true;
            mPausedSystem = false;
            //resumedSystemTime = System.currentTimeMillis();
        }


    }
}
