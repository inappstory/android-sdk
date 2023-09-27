package com.inappstory.sdk.stories.ui.reader.animations;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

public abstract class ReaderAnimation {

    Handler handler = new Handler(Looper.getMainLooper());

    public ReaderAnimation(View backgroundView, View foregroundView) {
        this.backgroundView = backgroundView;
        this.foregroundView = foregroundView;
    }

    public ReaderAnimation() {
    }

    View backgroundView = null;
    View foregroundView = null;

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

    public void start() {
        final long startTime = System.currentTimeMillis();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis() - startTime;
                if (time <= getAnimationDuration() + 50) {
                    float progress = Math.min((float) time / getAnimationDuration(), 1f);
                    if (isStart)
                        animatorUpdateStartAnimations(progress);
                    else
                        animatorUpdateFinishAnimations(1f - progress);
                    handler.post(this);
                } else {
                    if (listener != null)
                        listener.onAnimationEnd();
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
