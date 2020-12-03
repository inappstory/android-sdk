package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class OpenStoryByIdEvent {

    public int getIndex() {
        return index;
    }

    private int index;

    public OpenStoryByIdEvent(int index) {
        this.index = index;
    }
}
