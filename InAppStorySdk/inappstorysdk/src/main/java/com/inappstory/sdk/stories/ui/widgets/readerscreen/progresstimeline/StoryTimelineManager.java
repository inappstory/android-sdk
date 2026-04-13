package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import com.inappstory.sdk.core.data.IContentWithTimeline;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

public class StoryTimelineManager {
    private long timerStart;
    private long timerStartTimestamp;

    private long timerDuration;
    private boolean isActive;

    public void setContentWithTimeline(IContentWithTimeline contentWithTimeline) {
        this.contentWithTimeline = contentWithTimeline;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        setProgress(0);
    }

    IContentWithTimeline contentWithTimeline;

    public void startTimer(long timerStart, int currentIndex, long timerDuration) {
        this.currentIndex = currentIndex;
        this.timerStart = timerStart;
        this.timerDuration = timerDuration;
        /*Runnable hostVisibility = new Runnable() {
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
        loopedExecutor.task(timerTask);
    }

    LoopedExecutor loopedExecutor = new LoopedExecutor(1L, 17L);

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
        if (isSetViews) setProgressSync(0);
        else setProgress(0);
    }

    private int slidesCount;

    public void setHost(StoryTimeline host) {
        this.host = host;
    }

    public void clearHost(StoryTimeline host) {
        if (this.host == host)
            this.host = null;
    }

    StoryTimeline host;

    private void cancelTask() {
        loopedExecutor.cancelTask();
    }

    private void setProgressSync(float progress) {
        StoryTimeline localHost = host;
        if (localHost == null) return;
        if (contentWithTimeline != null) {
            localHost.setState(
                    new StoryTimelineState(
                            slidesCount,
                            currentIndex,
                            progress,
                            timerDuration,
                            contentWithTimeline.timelineIsHidden(),
                            contentWithTimeline.timelineForegroundColor(currentIndex),
                            contentWithTimeline.timelineBackgroundColor(currentIndex)
                    )
            );
        } else {
            localHost.setState(
                    new StoryTimelineState(
                            slidesCount,
                            currentIndex,
                            progress,
                            timerDuration
                    )
            );
        }
    }

    public void setProgress(final float progress) {
        StoryTimeline localHost = host;
        if (localHost != null) {
            localHost.post(new Runnable() {
                @Override
                public void run() {
                    setProgressSync(progress);
                }
            });
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
            loopedExecutor.freeExecutor();
        }
    };
}
