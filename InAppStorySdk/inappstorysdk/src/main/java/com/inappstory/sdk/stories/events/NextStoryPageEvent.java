package com.inappstory.sdk.stories.events;

import android.util.Log;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class NextStoryPageEvent {

    public int getStoryId() {
        return storyId;
    }

    public NextStoryPageEvent(int storyId) {
        Log.e("Story_Events", "NextStoryPageEvent " + storyId);
        this.storyId = storyId;
    }

    int storyId;

}
