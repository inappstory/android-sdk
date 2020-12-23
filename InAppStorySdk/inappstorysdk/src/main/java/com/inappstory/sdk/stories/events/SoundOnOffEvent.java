package com.inappstory.sdk.stories.events;

public class SoundOnOffEvent {
    public boolean isOn() {
        return on;
    }

    public SoundOnOffEvent(boolean on, int storyId) {
        this.on = on;
        this.storyId = storyId;
    }

    boolean on;

    public int getStoryId() {
        return storyId;
    }

    int storyId;
}
