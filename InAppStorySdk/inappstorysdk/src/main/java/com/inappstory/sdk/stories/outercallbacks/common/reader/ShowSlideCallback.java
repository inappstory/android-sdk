package com.inappstory.sdk.stories.outercallbacks.common.reader;

public abstract class ShowSlideCallback {
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    private String payload;

    public abstract void showSlide(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index);
}
