package com.inappstory.sdk.stories.events;

public class RestartStoryReaderEvent {


    int index;

    public int getId() {
        return id;
    }

    int id;

    public int getIndex() {
        return index;
    }

    public long getNewDuration() {
        return newDuration;
    }

    long newDuration;

    public RestartStoryReaderEvent(int id, int index, long newDuration) {
        this.index = index;
        this.id = id;
        this.newDuration = newDuration;
    }


}

