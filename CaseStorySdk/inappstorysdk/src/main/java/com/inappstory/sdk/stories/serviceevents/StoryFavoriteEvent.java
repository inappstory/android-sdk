package com.inappstory.sdk.stories.serviceevents;

public class StoryFavoriteEvent {
    public StoryFavoriteEvent(int id, boolean favStatus) {
        this.id = id;
        this.favStatus = favStatus;
    }

    public boolean getFavStatus() {
        return favStatus;
    }

    public boolean favStatus = false;

    public int getId() {
        return id;
    }

    int id;
}
