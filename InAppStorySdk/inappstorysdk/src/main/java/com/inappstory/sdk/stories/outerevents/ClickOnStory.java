package com.inappstory.sdk.stories.outerevents;

public class ClickOnStory extends BaseOuterEvent {
    public int getSource() {
        return source;
    }

    int source;

    public int getListIndex() {
        return listIndex;
    }

    int listIndex;

    public static final int LIST = 2;
    public static final int FAVORITE = 3;

    public ClickOnStory(int id, int listIndex, String title, String tags, int slidesCount, int source) {
        super(id, title, tags, slidesCount);
        this.source = source;
        this.listIndex = listIndex;
    }
}
