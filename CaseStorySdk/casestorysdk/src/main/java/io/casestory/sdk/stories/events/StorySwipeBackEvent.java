package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StorySwipeBackEvent {


    public int getNarrativeId() {
        return narrativeId;
    }

    public int narrativeId;

    public StorySwipeBackEvent(int narrativeId) {
        this.narrativeId = narrativeId;
    }


}
