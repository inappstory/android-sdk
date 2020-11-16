package io.casestory.sdk.stories.outerevents;

public class DislikeStory extends BaseOuterEvent {
    public int getIndex() {
        return index;
    }

    int index;

    public boolean getValue() {
        return value;
    }

    boolean value;

    public DislikeStory(int id, String title, String tags, int slidesCount, int index, boolean value) {
        super(id, title, tags, slidesCount);
        this.index = index;
        this.value = value;

    }
}
