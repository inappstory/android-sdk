package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import com.inappstory.sdk.core.data.IContentWithTimeline;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageTimelineState;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.StoryTimeline;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.StoryTimelineState;
import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StoryReaderPageTimelineManager {
    private final ScheduledTPEManager executorService = new ScheduledTPEManager();

    private long timerStart;
    private long timerStartTimestamp;

    private long timerDuration;
    private boolean isActive;

    private final Observable<StoryReaderPageTimelineState> storyReaderPageTimelineStateObservable =
            new Observable<>(null);

    public void addTimelineStateSubscriber(Observer<StoryReaderPageTimelineState> observer) {
        storyReaderPageTimelineStateObservable.subscribeAndGetValue(observer);
    }

    public void removeTimelineStateSubscriber(Observer<StoryReaderPageTimelineState> observer) {
        storyReaderPageTimelineStateObservable.unsubscribe(observer);
    }

    public StoryReaderPageTimelineManager(StoryReaderPageViewModel pageViewModel) {
        this.pageViewModel = pageViewModel;
    }

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
        setProgress(0);
    }

    private int slidesCount;

    StoryReaderPageViewModel pageViewModel;

    ScheduledFuture scheduledFuture;

    private void cancelTask() {
        if (scheduledFuture != null)
            scheduledFuture.cancel(false);
        scheduledFuture = null;
        executorService.shutdown();
    }

    private void setProgress(float progress) {
        if (contentWithTimeline != null) {
            storyReaderPageTimelineStateObservable.updateValue(
                    new StoryReaderPageTimelineState()
                            .slidesCount(slidesCount)
                            .currentIndex(currentIndex)
                            .currentProgress(progress)
                            .isHidden(contentWithTimeline.timelineIsHidden())
                            .timerDuration(timerDuration)
                            .foregroundColor(contentWithTimeline.timelineForegroundColor(currentIndex))
                            .backgroundColor(contentWithTimeline.timelineForegroundColor(currentIndex))
            );
        } else {
            storyReaderPageTimelineStateObservable.updateValue(
                    new StoryReaderPageTimelineState()
                            .slidesCount(slidesCount)
                            .currentIndex(currentIndex)
                            .currentProgress(progress)
                            .timerDuration(timerDuration)
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
