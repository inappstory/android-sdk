package com.inappstory.sdk.stories.outerevents;

public class FavoriteStory extends BaseOuterEvent {
    public int getIndex() {
        return index;
    }

    int index;

    public boolean getValue() {
        return value;
    }

    boolean value;

    public FavoriteStory(int id, String title, String tags, int slidesCount, int index, boolean value) {
        super(id, title, tags, slidesCount);
        this.index = index;
        this.value = value;
    }
}
