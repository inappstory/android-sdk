package com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay;

public class StoryDisplayState {
    public StoryDisplayState() {
        this.slideIndex = 0;
    }

    public StoryDisplayState(int slideIndex) {
        this.slideIndex = slideIndex;
    }


    public int slideIndex() {
        return slideIndex;
    }

    private int slideIndex;

    public void slideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
    }

    public boolean isFirstLoading() {
        return firstLoading;
    }

    public StoryDisplayState firstLoading() {
        this.firstLoading = true;
        return this;
    }

    private boolean firstLoading;
}
