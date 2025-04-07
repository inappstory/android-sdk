package com.inappstory.sdk.utils.ui.animation;

import android.animation.TimeInterpolator;

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

    private final ScheduledExecutorService animationTimerThread = Executors.newScheduledThreadPool(1);
    long startTime = 0;

    public void start(
            final long totalTime,
            final TimeInterpolator timeInterpolator
    ) {
        startTime = System.currentTimeMillis();
        listener.onStart();
        animationTimerThread.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        if (!animationTimerThread.isShutdown()) {
                            float progress = Math.min((System.currentTimeMillis() - startTime) / (1f * totalTime), 1f);
                            if (timeInterpolator != null) {
                                progress = timeInterpolator.getInterpolation(progress);
                            }
                            if (progress == 1f) {
                                listener.onUpdate(1f);
                                listener.onEnd();
                                animationTimerThread.shutdownNow();
                            } else {
                                listener.onUpdate(progress);
                            }
                        }
                    }
                },
                0,
                16,
                TimeUnit.MILLISECONDS
        );
    }
}
