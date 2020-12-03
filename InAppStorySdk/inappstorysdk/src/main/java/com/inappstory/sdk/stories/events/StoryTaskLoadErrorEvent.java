package com.inappstory.sdk.stories.events;

public class StoryTaskLoadErrorEvent {
    private int id;
    private int index;

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public StoryTaskLoadErrorEvent(int id, int index) {
        this.id = id;
        this.index = index;
    }
}
