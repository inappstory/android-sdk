package io.casestory.sdk.stories.events;

import android.util.Log;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class PrevStoryPageEvent {

    public int getStoryIndex() {
        return storyIndex;
    }

    public PrevStoryPageEvent(int storyIndex)
    {
        Log.d("Events", "prevNarrativePageEvent");
        this.storyIndex = storyIndex;
    }

    int storyIndex;

}
