package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class PrevStoryPageEvent {

    public int getStoryIndex() {
        return storyIndex;
    }

    public PrevStoryPageEvent(int storyIndex)
    {
        this.storyIndex = storyIndex;
    }

    int storyIndex;

}
