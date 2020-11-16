package io.casestory.sdk.stories.outerevents;

public class ShowSlide extends BaseOuterEvent {

    public int getIndex() {
        return index;
    }

    int index;

    public ShowSlide(int id, String title, String tags, int slidesCount, int index) {
        super(id, title, tags, slidesCount);
        this.index = index;
    }
}
