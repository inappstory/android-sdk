package com.inappstory.sdk.stories.outerevents;

public class ClickOnShareStory extends BaseOuterEvent {
    public int getIndex() {
        return index;
    }

    int index;

    public ClickOnShareStory(int id, String title, String tags, int slidesCount, int index) {
        super(id, title, tags, slidesCount);
        this.index = index;
    }
}
