package com.inappstory.sdk.stories.serviceevents;

public class LikeDislikeEvent {
    public LikeDislikeEvent(int id, int likeStatus) {
        this.id = id;
        this.likeStatus = likeStatus;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public int likeStatus = 0;

    public int getId() {
        return id;
    }

    int id;
}
