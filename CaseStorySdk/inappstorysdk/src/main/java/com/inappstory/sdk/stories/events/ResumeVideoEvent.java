package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 03.10.2018.
 */

public class ResumeVideoEvent {
    public int getIndex() {
        return index;
    }

    public int index;

    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public ResumeVideoEvent(int storyId, int index) {
        this.index = index;
        this.storyId = storyId;
    }

}
