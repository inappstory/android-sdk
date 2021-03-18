package com.inappstory.sdk.stories.outerevents;

public class BaseOuterEvent {
    int id;
    String title;
    String tags;
    int slidesCount;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTags() {
        return tags;
    }

    public int getSlidesCount() {
        return slidesCount;
    }

    public BaseOuterEvent(int id, String title, String tags, int slidesCount) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.slidesCount = slidesCount;
    }
}
