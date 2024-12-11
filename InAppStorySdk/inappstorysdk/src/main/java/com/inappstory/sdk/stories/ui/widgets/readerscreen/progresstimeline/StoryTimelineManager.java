package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.os.Looper;
import android.view.View;

import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryTimelineSettings;
import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StoryTimelineManager {
    private ScheduledTPEManager executorService = new ScheduledTPEManager();

    private long timerStart;
    private long timerStartTimestamp;

    private long timerDuration;
    private boolean isActive;
    private Story story;

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void startTimer(long timerStart, int currentIndex, long timerDuration) {
        this.currentIndex = currentIndex;
        this.timerStart = timerStart;
        this.timerDuration = timerDuration;
       /* final StoryTimeline host = getHost();
        Runnable hostVisibility = new Runnable() {
            @Override
            public void run() {
                if (slidesCount <= 1 && StoryTimelineManager.this.timerDuration == 0)
                    host.setVisibility(View.INVISIBLE);
                else
                    host.setVisibility(View.VISIBLE);
            }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            hostVisibility.run();
        } else {
            host.post(hostVisibility);
        }*/

        this.timerStartTimestamp = System.currentTimeMillis();
        this.isActive = true;
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

    public void setSlidesCount(final int slidesCount, boolean isSetViews) {
        this.slidesCount = slidesCount;
        final StoryTimeline host = getHost();

        if (isSetViews) {
            setProgressSync();
        } else {
            setProgress(0);
        }
    }

    private int slidesCount;

    public void setHost(StoryTimeline host) {
        synchronized (hostLock) {
            this.host = host;
        }
    }

    private StoryTimeline getHost() {
        synchronized (hostLock) {
            return host;
        }
    }

    private final Object hostLock = new Object();

    StoryTimeline host;

    ScheduledFuture scheduledFuture;

    private void cancelTask() {
        if (scheduledFuture != null)
            scheduledFuture.cancel(false);
        scheduledFuture = null;
        executorService.shutdown();
    }

    private void setProgress(final float progress) {
        final StoryTimeline host = getHost();
        if (host != null) {
            host.post(new Runnable() {
                @Override
                public void run() {
                    if (story == null) return;
             //       story.testMethod();
                    StoryTimelineSettings storyTimelineSettings = story.timelineSettings(currentIndex);
                    String foregroundColor = storyTimelineSettings != null ? storyTimelineSettings.foregroundColor : null;
                    String backgroundColor = storyTimelineSettings != null ? storyTimelineSettings.backgroundColor : null;
                    host.setState(
                            new StoryTimelineState(
                                    slidesCount,
                                    currentIndex,
                                    progress,
                                    timerDuration,
                                    foregroundColor,
                                    backgroundColor,
                                    story.isTimelineHidden
                            )
                    );
                }
            });
        }
    }

    private void setProgressSync() {
        final StoryTimeline host = getHost();
        if (host != null) {
            if (story == null) return;
           // story.testMethod();
            StoryTimelineSettings storyTimelineSettings = story.timelineSettings(currentIndex);
            String foregroundColor = storyTimelineSettings != null ? storyTimelineSettings.foregroundColor : null;
            String backgroundColor = storyTimelineSettings != null ? storyTimelineSettings.backgroundColor : null;
            host.setState(
                    new StoryTimelineState(
                            slidesCount,
                            currentIndex,
                            0,
                            timerDuration,
                            foregroundColor,
                            backgroundColor,
                            story.isTimelineHidden
                    )
            );
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
