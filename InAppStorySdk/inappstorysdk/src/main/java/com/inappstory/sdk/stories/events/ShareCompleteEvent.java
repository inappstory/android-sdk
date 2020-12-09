package com.inappstory.sdk.stories.events;

public class ShareCompleteEvent {

    public ShareCompleteEvent(int storyId, String id, boolean isSuccess) {
        this.id = id;
        this.storyId = storyId;
        this.isSuccess = isSuccess;
    }

    public ShareCompleteEvent(String id) {
        this.id = id;
        this.isSuccess = true;
    }

    public int storyId;
    String id;
    boolean isSuccess;

    public String getId() {
        return id;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
