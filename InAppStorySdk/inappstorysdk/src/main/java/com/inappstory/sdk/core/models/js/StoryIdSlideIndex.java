package com.inappstory.sdk.core.models.js;

public class StoryIdSlideIndex {
    public StoryIdSlideIndex() {
    }

    public int id;
    public int index;

    public StoryIdSlideIndex(int id, int index) {
        this.id = id;
        this.index = index;
    }

    public StoryIdSlideIndex(int id) {
        this.id = id;
        this.index = 0;
    }
}
