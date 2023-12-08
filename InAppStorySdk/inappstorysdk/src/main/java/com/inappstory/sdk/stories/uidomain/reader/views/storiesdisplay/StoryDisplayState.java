package com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay;

public class StoryDisplayState {
    public StoryDisplayState(
            int storyId
    ) {
        this.storyId = storyId;
        this.slideIndex = 0;
        this.firstLoading = true;
    }

    public int storyId() {
        return storyId;
    }

    private int storyId;

    public int slideIndex() {
        return slideIndex;
    }

    private int slideIndex;

    public void slideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
    }

    public void loaded() {
        this.firstLoading = false;
    }

    public boolean firstLoading() {
        return firstLoading;
    }

    private boolean firstLoading;
}
