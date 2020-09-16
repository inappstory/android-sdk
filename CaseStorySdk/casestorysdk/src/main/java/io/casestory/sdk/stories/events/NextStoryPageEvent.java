package io.casestory.sdk.stories.events;

import android.util.Log;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class NextStoryPageEvent {

    public int getNarrativeIndex() {
        return narrativeIndex;
    }

    public NextStoryPageEvent(int narrativeIndex) {
        Log.d("Events", "nextNarrativePageEvent");

        this.narrativeIndex = narrativeIndex;
    }

    int narrativeIndex;

}
