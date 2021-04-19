package com.inappstory.sdk.stories.events;

public class GameCompleteEvent {
    String data;

    public String getData() {
        return data;
    }

    public int getStoryId() {
        return storyId;
    }

    public int getSlideIndex() {
        return slideIndex;
    }

    int storyId;

    public GameCompleteEvent(String data, int storyId, int slideIndex) {
        this.data = data;
        this.storyId = storyId;
        this.slideIndex = slideIndex;
    }

    int slideIndex;
}
