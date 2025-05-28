package com.inappstory.sdk.share;

public abstract class IShareCompleteListener {
    public String getShareId() {
        return shareId;
    }

    private String shareId;

    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public IShareCompleteListener(String shareId, int storyId) {
        this.shareId = shareId;
        this.storyId = storyId;
    }

    public void complete(boolean shared) {
        complete(getShareId(), shared);
    }

    public abstract void complete(String shareId, boolean shared);
}
