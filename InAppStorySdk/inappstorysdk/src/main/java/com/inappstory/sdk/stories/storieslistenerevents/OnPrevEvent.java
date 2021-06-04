package com.inappstory.sdk.stories.storieslistenerevents;

public class OnPrevEvent {
    public OnPrevEvent(int id) {
        this.storyId = id;
    }

    public int getStoryId() {
        return storyId;
    }

    private int storyId;
}
