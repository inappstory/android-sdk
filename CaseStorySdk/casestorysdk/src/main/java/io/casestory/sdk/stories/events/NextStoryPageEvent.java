package io.casestory.sdk.stories.events;

import android.util.Log;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class NextStoryPageEvent {

    public int getStoryIndex() {
        return storyIndex;
    }

    public NextStoryPageEvent(int storyIndex) {

        this.storyIndex = storyIndex;
    }

    int storyIndex;

}
