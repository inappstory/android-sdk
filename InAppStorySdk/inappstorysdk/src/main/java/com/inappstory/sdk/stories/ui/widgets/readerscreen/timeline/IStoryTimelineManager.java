package com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline;

import java.util.List;

public interface IStoryTimelineManager {
    void active(boolean active);
    void startSegment(int index);

    void setSegment(int index);
    void resume();
    void pause();
    void stop();

    void next();

    void prev();
    void clear();
    void setDurations(List<Integer> durations, boolean started);

    void setCurrentPosition(double position);

    void setSlidesCount(int slidesCount);
    void resetDurations();
}
