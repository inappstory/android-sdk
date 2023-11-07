package com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline;


import java.util.ArrayList;

public class StoryTimelineState {
    private final Object lock = new Object();

    private boolean timelineIsActive = false;

    private int slidesCount = 0;
    private ArrayList<Integer> currentStoryDurations = new ArrayList<>();
    private float currentSlideProgress = 0;
    private int currentSlideIndex = 0;

    public boolean isTimelineHidden() {
        synchronized (lock) {
            return hideTimeline;
        }
    }

    private boolean hideTimeline = false;

    private StoryTimelineSegmentState currentSlideState = StoryTimelineSegmentState.EMPTY;

    public boolean timelineIsActive() {
        synchronized (lock) {
            return timelineIsActive;
        }
    }

    public void setTimelineIsActive(boolean timelineIsActive) {
        synchronized (lock) {
            this.timelineIsActive = timelineIsActive;
        }
    }

    public int getSlidesCount() {
        synchronized (lock) {
            return slidesCount;
        }
    }

    public void setSlidesCount(int slidesCount) {
        synchronized (lock) {
            this.slidesCount = slidesCount;
        }
    }

    public long getCurrentStoryDuration() {
        synchronized (lock) {
            return currentStoryDurations.get(currentSlideIndex);
        }
    }

    public ArrayList<Integer> getCurrentStoryDurations() {
        synchronized (lock) {
            return currentStoryDurations;
        }
    }

    public void setCurrentStoryDurations(ArrayList<Integer> currentStoryDurations) {
        synchronized (lock) {

            this.currentStoryDurations = currentStoryDurations;
            this.slidesCount = currentStoryDurations.size();
            if (currentStoryDurations.isEmpty()) {
                this.hideTimeline = true;
            } else if (currentStoryDurations.size() == 1) {
                this.hideTimeline = currentStoryDurations.get(0) == 0;
            } else {
                this.hideTimeline = false;
            }
        }
    }

    public float getCurrentSlideProgress() {
        synchronized (lock) {
            return currentSlideProgress;
        }
    }

    public void setCurrentSlideProgress(float currentSlideProgress) {
        synchronized (lock) {
            this.currentSlideProgress = currentSlideProgress;
        }
    }

    public int getCurrentSlideIndex() {
        synchronized (lock) {
            return currentSlideIndex;
        }
    }

    public void setCurrentSlideIndex(int currentSlideIndex) {
        synchronized (lock) {
            this.currentSlideIndex = currentSlideIndex;
            this.currentSlideProgress = 0;
            this.currentSlideState = StoryTimelineSegmentState.EMPTY;
        }
    }

    public StoryTimelineSegmentState getCurrentSlideState() {
        synchronized (lock) {
            return currentSlideState;
        }
    }

    public void setCurrentSlideState(StoryTimelineSegmentState currentSlideState) {
        synchronized (lock) {
            this.currentSlideState = currentSlideState;
            if (currentSlideState == StoryTimelineSegmentState.EMPTY) {
                currentSlideProgress = 0f;
            } else if (currentSlideState == StoryTimelineSegmentState.FILLED) {
                currentSlideProgress = 1f;
            }
        }
    }

    public StoryTimelineState() {
    }

    public void clearTimelineProgress() {
        synchronized (lock) {
            currentSlideIndex = 0;
            currentSlideState = StoryTimelineSegmentState.EMPTY;
            currentSlideProgress = 0f;
        }
    }

    public StoryTimelineState(
            boolean timelineIsActive,
            int slidesCount,
            ArrayList<Integer> currentStoryDurations,
            float currentSlideProgress,
            int currentSlideIndex,
            StoryTimelineSegmentState currentSlideState
    ) {
        synchronized (this) {
            this.timelineIsActive = timelineIsActive;
            this.slidesCount = slidesCount;
            this.currentStoryDurations = currentStoryDurations;
            this.currentSlideProgress = currentSlideProgress;
            this.currentSlideIndex = currentSlideIndex;
            this.currentSlideState = currentSlideState;
        }
    }
}
