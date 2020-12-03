package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 03.10.2018.
 */

public class StopVideoEvent {
    public int getIndex() {
        return index;
    }

    public int index;

    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public StopVideoEvent(int storyId, int index) {
        this.index = index;
        this.storyId = storyId;
    }

}
