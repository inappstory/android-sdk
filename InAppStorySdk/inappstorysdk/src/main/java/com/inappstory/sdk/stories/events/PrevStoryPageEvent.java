package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class PrevStoryPageEvent {

    public int getStoryId() {
        return storyId;
    }

    public PrevStoryPageEvent(int storyId)
    {
        this.storyId = storyId;
    }

    int storyId;

}
