package com.inappstory.sdk.utils.animation;

import android.animation.TimeInterpolator;

import com.inappstory.sdk.stories.utils.LoopedExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IndependentAnimator {
    private final IndependentAnimatorListener listener;

    public IndependentAnimator(
            IndependentAnimatorListener listener
    ) {
        this.listener = listener;
    }

    long startTime = 0;

    public void start(
            final long totalTime,
            final TimeInterpolator timeInterpolator
    ) {
        startTime = System.currentTimeMillis();
        listener.onStart();
        final LoopedExecutor loopedExecutor = new LoopedExecutor(0, 16);
        Runnable animationRunnable = new Runnable() {
            @Override
            public void run() {
                float progress = Math.min((System.currentTimeMillis() - startTime) / (1f * totalTime), 1f);
                if (timeInterpolator != null) {
                    progress = timeInterpolator.getInterpolation(progress);
                }
                if (progress == 1f) {
                    listener.onUpdate(1f);
                    listener.onEnd();
                    loopedExecutor.shutdown();
                } else {
                    listener.onUpdate(progress);
                    loopedExecutor.freeExecutor();
                }
            }
        };
        loopedExecutor.task(animationRunnable);
    }
}
