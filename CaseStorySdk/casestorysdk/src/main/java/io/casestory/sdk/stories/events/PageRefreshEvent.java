package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 26.07.2018.
 */

public class PageRefreshEvent {
    public int getNarrativeId() {
        return narrativeId;
    }

    private int narrativeId;

    public PageRefreshEvent(int narrativeId) {
        this.narrativeId = narrativeId;
    }
}
