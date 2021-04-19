package com.inappstory.sdk.stories.outerevents;

public class FinishGame extends BaseOuterEvent {

    public String getResult() {
        return result;
    }

    String result;

    public int getIndex() {
        return index;
    }

    int index;

    public FinishGame(int id,
                      String title,
                      String tags,
                      int slidesCount,
                      int index,
                      String result) {
        super(id, title, tags, slidesCount);
        this.index = index;
        this.result = result;
    }
}
