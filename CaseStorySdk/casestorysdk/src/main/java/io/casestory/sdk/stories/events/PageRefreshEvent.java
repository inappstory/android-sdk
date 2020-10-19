package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 26.07.2018.
 */

public class PageRefreshEvent {
    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public int getIndex() {
        return index;
    }

    private int index;

    public PageRefreshEvent(int storyId, int index) {
        this.storyId = storyId;
        this.index = index;
    }
}
