package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 26.07.2018.
 */

public class PageByIndexRefreshEvent {
    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public PageByIndexRefreshEvent(int storyId) {
        this.storyId = storyId;
    }
}
