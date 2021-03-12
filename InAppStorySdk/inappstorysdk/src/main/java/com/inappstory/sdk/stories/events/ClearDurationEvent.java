package com.inappstory.sdk.stories.events;

public class ClearDurationEvent {


    int index;

    public int getId() {
        return id;
    }

    int id;

    public int getIndex() {
        return index;
    }


    public ClearDurationEvent(int id, int index) {
        this.index = index;
        this.id = id;
    }


}

