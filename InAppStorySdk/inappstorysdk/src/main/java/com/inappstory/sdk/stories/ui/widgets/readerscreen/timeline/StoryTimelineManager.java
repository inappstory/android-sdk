package com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StoryTimelineManager implements IStoryTimelineManager {
    StoryTimelineState timelineState = new StoryTimelineState();
    private ArrayList<Integer> startedDurations = new ArrayList<>();

    private long resumedTime = System.currentTimeMillis();

    private long startedTime = System.currentTimeMillis();
    private long restTime = 0;

    @Override
    public void active(boolean active) {
        if (timelineState.timelineIsActive() == active) return;
        if (active) {
            handler.post(loopedTimer);
        } else {
            handler.removeCallbacks(loopedTimer);
            stop();
        }
        timelineState.setTimelineIsActive(active);
    }

    boolean paused = false;

    Handler handler = new Handler(Looper.myLooper());

    Runnable loopedTimer = new Runnable() {
        @Override
        public void run() {
            if (paused) return;
            if (!timelineState.timelineIsActive()) return;
            if (timelineState.getCurrentStoryDurations().size() > 0 &&
                    timelineState.getCurrentStoryDuration() > 0 &&
                    timelineState.getCurrentSlideState() == StoryTimelineSegmentState.ANIMATED) {
                long spentTime = System.currentTimeMillis() - resumedTime;
                if (spentTime > restTime) {
                    next();
                } else {
                    updateProgress(spentTime);
                }
            }
            if (paused) return;
            if (!timelineState.timelineIsActive()) return;
            handler.post(this);
        }
    };

    private void updateProgress(long spentTime) {
        if (paused) return;
        float progress = Math.max(0f, Math.min(1f, 1f - 1f * (restTime - spentTime) /
                (timelineState.getCurrentStoryDuration())));
        timelineState.setCurrentSlideProgress(progress);
    }

    @Override
    public void startSegment(int index) {
        if (paused) return;
        if (index > timelineState.getCurrentStoryDurations().size() - 1 || index < 0) return;
        startedTime = resumedTime = System.currentTimeMillis();
        timelineState.setCurrentSlideIndex(index);
        restTime = timelineState.getCurrentStoryDuration();
        if (restTime > 0)
            timelineState.setCurrentSlideState(StoryTimelineSegmentState.ANIMATED);
    }

    @Override
    public void setSegment(int index) {
        if (index > timelineState.getCurrentStoryDurations().size() - 1 || index < 0) return;
        timelineState.setCurrentSlideIndex(index);
        timelineState.setCurrentSlideState(StoryTimelineSegmentState.EMPTY);
    }

    @Override
    public void resume() {
        resumedTime = System.currentTimeMillis();
        paused = false;
        restTime = (long) (
                (1f - timelineState.getCurrentSlideProgress()) *
                        timelineState
                                .getCurrentStoryDurations()
                                .get(timelineState.getCurrentSlideIndex())
        );
        handler.post(loopedTimer);
    }

    @Override
    public void pause() {
        handler.removeCallbacks(loopedTimer);
    }

    @Override
    public void stop() {
        timelineState.setCurrentSlideState(StoryTimelineSegmentState.EMPTY);
    }

    public void setNextTimelineCallback(TimelineCallback timelineCallback) {
        this.timelineCallback = timelineCallback;
    }

    private TimelineCallback timelineCallback;

    @Override
    public void next() {
        timelineState.setCurrentSlideState(StoryTimelineSegmentState.FILLED);
        if (timelineCallback != null)
            timelineCallback.nextSlide(
                    timelineState.getCurrentSlideIndex() + 1
            );
    }

    @Override
    public void prev() {
        if (timelineState.getCurrentSlideIndex() > 0)
            startSegment(timelineState.getCurrentSlideIndex() - 1);
    }

    @Override
    public void clear() {
        timelineState.clearTimelineProgress();
    }

    @Override
    public void setDurations(List<Integer> durations, boolean started) {
        if (started)
            startedDurations = new ArrayList<>(durations);
        timelineState.setCurrentStoryDurations(new ArrayList<>(durations));
    }

    @Override
    public void setSlidesCount(int slidesCount) {
        timelineState.setSlidesCount(slidesCount);
    }

    @Override
    public void resetDurations() {
        timelineState.setCurrentStoryDurations(new ArrayList<>(startedDurations));
    }
}
