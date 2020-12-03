package com.inappstory.sdk.stories.events;

public class ChangeIndexEvent {

    public int getIndex() {
        return index;
    }

    private int index;

    public ChangeIndexEvent(int index) {
        this.index = index;
    }
}
