package com.inappstory.sdk.core.repository.statistic;

public class StatisticEvent {
    public int eventType;
    public int storyId;
    public int index;
    public long timer;

    public StatisticEvent() {
        this.timer = System.currentTimeMillis();
    }

    public StatisticEvent(int eventType, int storyId, int index) {
        this.eventType = eventType;
        this.storyId = storyId;
        this.index = index;
        this.timer = System.currentTimeMillis();
    }

    public StatisticEvent(int eventType, int storyId, int index, long timer) {
        this.eventType = eventType;
        this.storyId = storyId;
        this.index = index;
        this.timer = timer;
    }
}