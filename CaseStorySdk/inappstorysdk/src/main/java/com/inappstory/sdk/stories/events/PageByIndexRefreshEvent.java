package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 26.07.2018.
 */

public class PageByIndexRefreshEvent {
    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public int getIndex() {
        return index;
    }

    private int index;

    public PageByIndexRefreshEvent(int storyId, int index) {
        this.storyId = storyId;
        this.index = index;
    }
}
