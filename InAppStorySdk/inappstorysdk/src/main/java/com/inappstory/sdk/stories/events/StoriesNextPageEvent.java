package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 03.10.2018.
 */

public class StoriesNextPageEvent {
    public StoriesNextPageEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    private int index;
}
