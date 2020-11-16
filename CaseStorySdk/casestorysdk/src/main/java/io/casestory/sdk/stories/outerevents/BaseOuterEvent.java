package io.casestory.sdk.stories.outerevents;

public class BaseOuterEvent {
    int id;
    String title;
    String tags;
    int slidesCount;

    public BaseOuterEvent(int id, String title, String tags, int slidesCount) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.slidesCount = slidesCount;
    }
}
