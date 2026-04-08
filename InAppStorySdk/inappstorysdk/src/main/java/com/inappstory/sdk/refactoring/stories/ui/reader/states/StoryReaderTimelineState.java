package com.inappstory.sdk.refactoring.stories.ui.reader.states;


import com.inappstory.sdk.core.network.content.models.StorySlideTimeline;

public class StoryReaderTimelineState {
    int slidesCount;
    int currentIndex;
    long timerDuration;
    float currentProgress;
    boolean isHidden;
    String foregroundColor;
    String backgroundColor;

    public StoryReaderTimelineState(int slidesCount, int currentIndex, float currentProgress, long timerDuration) {
        this.slidesCount = slidesCount;
        this.currentIndex = currentIndex;
        this.timerDuration = timerDuration;
        this.currentProgress = currentProgress;
    }

    public StoryReaderTimelineState(
            int slidesCount,
            int currentIndex,
            float currentProgress,
            long timerDuration,
            boolean isHidden,
            String foregroundColor,
            String backgroundColor
    ) {
        this.slidesCount = slidesCount;
        this.currentIndex = currentIndex;
        this.timerDuration = timerDuration;
        this.currentProgress = currentProgress;
        this.isHidden = isHidden;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public String backgroundColor() {
        if (backgroundColor == null) return StorySlideTimeline.DEFAULT_TIMELINE_BACKGROUND_COLOR;
        return backgroundColor;
    }

    public String foregroundColor() {
        if (foregroundColor == null) return StorySlideTimeline.DEFAULT_TIMELINE_FOREGROUND_COLOR;
        return foregroundColor;
    }
}
