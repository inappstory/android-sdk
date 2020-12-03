package com.inappstory.sdk.stories.events;

public class PageTaskToLoadEvent {
    int id;
    int index;
    boolean isLoaded;

    public boolean isLoaded() {
        return isLoaded;
    }

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public PageTaskToLoadEvent(int id, int index, boolean isLoaded) {
        this.id = id;
        this.index = index;
        this.isLoaded = isLoaded;
    }
}
