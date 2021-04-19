package com.inappstory.sdk.stories.outerevents;

public class CloseGame extends BaseOuterEvent {
    public int getIndex() {
        return index;
    }

    int index;

    public CloseGame(int id, String title, String tags, int slidesCount, int index) {
        super(id, title, tags, slidesCount);
        this.index = index;
    }
}
