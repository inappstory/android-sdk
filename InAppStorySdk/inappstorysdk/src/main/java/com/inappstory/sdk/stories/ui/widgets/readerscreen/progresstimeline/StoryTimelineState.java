package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;


public class StoryTimelineState {
    int slidesCount;
    int currentIndex;
    long timerDuration;
    float currentProgress;

    public StoryTimelineState(int slidesCount, int currentIndex, float currentProgress, long timerDuration) {
        this.slidesCount = slidesCount;
        this.currentIndex = currentIndex;
        this.timerDuration = timerDuration;
        this.currentProgress = currentProgress;
    }
}
