package com.inappstory.sdk.externalapi.storylist;

public class IASStoryListSessionData {
    public String feed() {
        return feed;
    }

    public float previewAspectRatio() {
        return previewAspectRatio;
    }

    public IASStoryListSessionData previewAspectRatio(float previewAspectRatio) {
        this.previewAspectRatio = previewAspectRatio;
        return this;
    }

    public IASStoryListSessionData feed(String feed) {
        this.feed = feed;
        return this;
    }

    private float previewAspectRatio;

    private String feed;
}
