package com.inappstory.sdk.stories.ui.reader.animations;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.MainThread;

public abstract class ReaderAnimation {

    Handler handler = new Handler();

    public ReaderAnimation(View backgroundView) {
        this.backgroundView = backgroundView;
    }

    public ReaderAnimation() {
    }

    View backgroundView = null;

    boolean isStart = false;

    ValueAnimator animator;

    public ReaderAnimation setAnimations(final boolean isStart) {
        this.isStart = isStart;
        return this;
    }


    HandlerAnimatorListener listener;

    public ReaderAnimation setListener(HandlerAnimatorListener listener) {
        this.listener = listener;
        //if (animator != null) animator.addListener(listener);
        return this;
    }

    @MainThread
    public void start() {
        final long startTime = System.currentTimeMillis();
        listener.onAnimationStart();
        if (getAnimationDuration() == 0) {
            listener.onAnimationEnd();
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {

                long time = System.currentTimeMillis() - startTime;
                final float progress = Math.min((float) time / getAnimationDuration(), 1f);
                if (isStart)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            animatorUpdateStartAnimations(progress);
                        }
                    });
                else
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            animatorUpdateFinishAnimations(1f - progress);
                        }
                    });

                if (progress == 1f) {
                    if (listener != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onAnimationEnd();
                            }
                        });

                    }
                } else {
                    handler.post(this);
                }
            }
        });
    }

    abstract void animatorUpdateStartAnimations(float value);

    abstract void animatorUpdateFinishAnimations(float value);

    private int getAnimationDuration() {
        if (isStart) return getStartAnimationDuration();
        else return getFinishAnimationDuration();
    }

    abstract int getStartAnimationDuration();

    abstract int getFinishAnimationDuration();

}
