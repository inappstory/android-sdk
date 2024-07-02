package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;


public class ComplexTimelineState {
    int slidesCount;
    int currentIndex;
    float currentProgress;

    public ComplexTimelineState(int slidesCount, int currentIndex, float currentProgress) {
        this.slidesCount = slidesCount;
        this.currentIndex = currentIndex;
        this.currentProgress = currentProgress;
    }
}
