package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.view.View;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StoryTimelineManager {
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private long timerStart;
    private long timerStartTimestamp;

    private long timerDuration;
    private boolean isActive;

    public void startTimer(long timerStart, int currentIndex, long timerDuration) {
        this.currentIndex = currentIndex;
        this.timerStart = timerStart;
        this.timerDuration = timerDuration;
        if (slidesCount <= 1 && timerDuration == 0)
            host.setVisibility(View.INVISIBLE);
        else
            host.setVisibility(View.VISIBLE);
        this.timerStartTimestamp = System.currentTimeMillis();
        this.isActive = true;
        if (executorService.isShutdown()) {
            executorService = new ScheduledThreadPoolExecutor(1);
        }
        scheduledFuture = executorService.scheduleAtFixedRate(
                timerTask,
                1L,
                17L,
                TimeUnit.MILLISECONDS
        );
    }

    public void stopTimer() {
        cancelTask();
        isActive = false;
    }

    public void clearTimer() {
        setProgress(0);
    }

    private int currentIndex;

    public void setSlidesCount(int slidesCount) {
        this.slidesCount = slidesCount;
        if (slidesCount <= 1) host.setVisibility(View.INVISIBLE);
        else host.setVisibility(View.VISIBLE);
        setProgress(0);
    }

    private int slidesCount;

    public void setHost(StoryTimeline host) {
        this.host = host;
    }

    StoryTimeline host;

    ScheduledFuture scheduledFuture;

    private void cancelTask() {
        if (scheduledFuture != null)
            scheduledFuture.cancel(false);
        scheduledFuture = null;
        executorService.shutdown();
    }

    private void setProgress(float progress) {
        if (host != null) {
            host.setState(new StoryTimelineState(slidesCount, currentIndex, progress, timerDuration));
        }
    }

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            float currentTime = (timerStart + System.currentTimeMillis() - timerStartTimestamp);
            if (!isActive || timerDuration > 0 && currentTime >= timerDuration) {
                cancelTask();
            } else {
                setProgress(currentTime / timerDuration);
            }
        }
    };
}
