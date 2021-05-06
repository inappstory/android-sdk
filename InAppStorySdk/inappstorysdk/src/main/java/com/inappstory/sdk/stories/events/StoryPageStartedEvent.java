package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StoryPageStartedEvent {


    public int getIndex() {
        return index;
    }

    public int index;

    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public StoryPageStartedEvent(int storyId, int index) {
        this.index = index;
        this.storyId = storyId;
    }


}
