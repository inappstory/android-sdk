package io.casestory.sdk.stories.events;

import android.util.Log;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class PrevStoryPageEvent {

    public int getNarrativeIndex() {
        return narrativeIndex;
    }

    public PrevStoryPageEvent(int narrativeIndex)
    {
        Log.d("Events", "prevNarrativePageEvent");
        this.narrativeIndex = narrativeIndex;
    }

    int narrativeIndex;

}
