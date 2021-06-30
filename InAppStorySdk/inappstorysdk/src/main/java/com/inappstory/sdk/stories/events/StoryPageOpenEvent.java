package com.inappstory.sdk.stories.events;

import android.util.Log;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StoryPageOpenEvent {
    public boolean isNext;
    public boolean isPrev;

    public int getIndex() {
        return index;
    }

    public int index;

    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public StoryPageOpenEvent(int storyId, int index, boolean isNext, boolean isPrev) {
        this.isNext = isNext;
        this.isPrev = isPrev;
        this.index = index;
        this.storyId = storyId;
        Log.e("Story_Events", "StoryPageOpenEvent " + storyId + " " + index);
    }

    public StoryPageOpenEvent(int storyId, int index) {
        this.index = index;
        this.storyId = storyId;
    }


}
