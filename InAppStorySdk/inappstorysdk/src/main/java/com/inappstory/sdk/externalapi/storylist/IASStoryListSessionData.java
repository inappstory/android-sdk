package com.inappstory.sdk.externalapi.storylist;

public class IASStoryListSessionData {
    public float previewAspectRatio() {
        return previewAspectRatio;
    }

    public IASStoryListSessionData previewAspectRatio(float previewAspectRatio) {
        this.previewAspectRatio = previewAspectRatio;
        return this;
    }

    private float previewAspectRatio;
}
