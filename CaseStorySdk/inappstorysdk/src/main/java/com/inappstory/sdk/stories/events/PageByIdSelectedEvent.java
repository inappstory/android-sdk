package com.inappstory.sdk.stories.events;

public class PageByIdSelectedEvent {

    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public boolean isOnlyResume() {
        return onlyResume;
    }

    private boolean onlyResume;

    public PageByIdSelectedEvent(int storyId, boolean onlyResume) {
        this.storyId = storyId;
        this.onlyResume = onlyResume;
    }
}
