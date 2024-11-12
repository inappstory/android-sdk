package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;


import static com.inappstory.sdk.stories.api.models.StoryTimelineSettings.DEFAULT_BG_COLOR;
import static com.inappstory.sdk.stories.api.models.StoryTimelineSettings.DEFAULT_FG_COLOR;

public class StoryTimelineState {
    int slidesCount;
    int currentIndex;
    String foregroundColor;
    String backgroundColor;
    boolean isHidden;
    long timerDuration;
    float currentProgress;

    public StoryTimelineState(
            int slidesCount,
            int currentIndex,
            float currentProgress,
            long timerDuration,
            String foregroundColor,
            String backgroundColor,
            boolean isHidden
    ) {
        this.slidesCount = slidesCount;
        this.currentIndex = currentIndex;
        this.timerDuration = timerDuration;
        this.currentProgress = currentProgress;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.isHidden = isHidden;
    }

    public String getBackgroundColor() {
        if (backgroundColor == null) return DEFAULT_BG_COLOR;
        return backgroundColor;
    }

    public String getForegroundColor() {
        if (foregroundColor == null) return DEFAULT_FG_COLOR;
        return foregroundColor;
    }

    @Override
    public String toString() {
        return "StoryTimelineState{" +
                "slidesCount=" + slidesCount +
                ", currentIndex=" + currentIndex +
                ", foregroundColor='" + foregroundColor + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", isHidden=" + isHidden +
                ", timerDuration=" + timerDuration +
                ", currentProgress=" + currentProgress +
                '}';
    }
}
