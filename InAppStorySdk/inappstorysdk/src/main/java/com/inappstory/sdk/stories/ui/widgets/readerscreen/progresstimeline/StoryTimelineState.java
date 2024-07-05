package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;


public class StoryTimelineState {
    int slidesCount;
    int currentIndex;
    float currentProgress;

    public StoryTimelineState(int slidesCount, int currentIndex, float currentProgress) {
        this.slidesCount = slidesCount;
        this.currentIndex = currentIndex;
        this.currentProgress = currentProgress;
    }
}
