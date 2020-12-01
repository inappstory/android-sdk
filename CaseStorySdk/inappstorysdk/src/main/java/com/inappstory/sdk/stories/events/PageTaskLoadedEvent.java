package com.inappstory.sdk.stories.events;

public class PageTaskLoadedEvent {
    int id;
    int index;

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public PageTaskLoadedEvent(int id, int index) {
        this.id = id;
        this.index = index;
    }
}
