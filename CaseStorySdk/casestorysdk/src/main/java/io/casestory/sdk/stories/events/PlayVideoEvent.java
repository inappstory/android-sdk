package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 03.10.2018.
 */

public class PlayVideoEvent {
    public int getIndex() {
        return index;
    }

    public int index;

    public int getNarrativeId() {
        return narrativeId;
    }

    public int narrativeId;

    public PlayVideoEvent(int narrativeId, int index) {
        this.index = index;
        this.narrativeId = narrativeId;
    }

}
