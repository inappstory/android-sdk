package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StoryTimerReverseEvent {


    public int getIndex() {
        return index;
    }

    private int index;

    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public StoryTimerReverseEvent(int storyId, int index) {
        this.index = index;
        this.storyId = storyId;
    }


}
