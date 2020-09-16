package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StoryPageLoadedEvent {


    public int getIndex() {
        return index;
    }

    public int index;

    public int getNarrativeId() {
        return narrativeId;
    }

    public int narrativeId;

    public StoryPageLoadedEvent(int narrativeId, int index) {
        this.index = index;
        this.narrativeId = narrativeId;
    }


}
