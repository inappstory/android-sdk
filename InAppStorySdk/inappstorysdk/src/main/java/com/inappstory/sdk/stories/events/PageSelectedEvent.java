package com.inappstory.sdk.stories.events;

public class PageSelectedEvent {

    public int getIndex() {
        return index;
    }

    private int index;

    public PageSelectedEvent(int index) {
        this.index = index;
    }
}
