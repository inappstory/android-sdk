package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 26.07.2018.
 */

public class PageRefreshEvent {
    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public PageRefreshEvent(int storyId) {
        this.storyId = storyId;
    }
}
