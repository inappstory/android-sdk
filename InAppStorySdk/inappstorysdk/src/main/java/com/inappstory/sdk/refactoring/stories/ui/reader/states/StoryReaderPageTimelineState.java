package com.inappstory.sdk.refactoring.stories.ui.reader.states;

public class StoryReaderPageTimelineState {

    private int slidesCount;
    private int currentIndex;
    private long timerDuration;
    private float currentProgress;
    private boolean isHidden;
    private String foregroundColor;
    private String backgroundColor;


    public StoryReaderPageTimelineState slidesCount(int slidesCount) {
        this.slidesCount = slidesCount;
        return this;
    }

    public StoryReaderPageTimelineState currentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        return this;
    }

    public StoryReaderPageTimelineState timerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
        return this;
    }

    public StoryReaderPageTimelineState currentProgress(float currentProgress) {
        this.currentProgress = currentProgress;
        return this;
    }

    public StoryReaderPageTimelineState isHidden(boolean hidden) {
        isHidden = hidden;
        return this;
    }

    public StoryReaderPageTimelineState foregroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public StoryReaderPageTimelineState backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public int slidesCount() {
        return slidesCount;
    }

    public int currentIndex() {
        return currentIndex;
    }

    public long timerDuration() {
        return timerDuration;
    }

    public float currentProgress() {
        return currentProgress;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public String backgroundColor() {
        if (backgroundColor == null)
            return "#ffffff8a";
        return backgroundColor;
    }

    public String foregroundColor() {
        if (foregroundColor == null)
            return "#ffffffff";
        return foregroundColor;
    }

    @Override
    public String toString() {
        return "StoryReaderPageTimelineState{" +
                "slidesCount=" + slidesCount +
                ", currentIndex=" + currentIndex +
                ", timerDuration=" + timerDuration +
                ", currentProgress=" + currentProgress +
                ", isHidden=" + isHidden +
                ", foregroundColor='" + foregroundColor + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                '}';
    }

    public StoryReaderPageTimelineState copy() {
        return new StoryReaderPageTimelineState()
                .foregroundColor(foregroundColor)
                .backgroundColor(backgroundColor)
                .isHidden(isHidden)
                .currentIndex(currentIndex)
                .slidesCount(slidesCount)
                .currentProgress(currentProgress)
                .timerDuration(timerDuration);
    }
}
