package com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay;

public class SlideLoadState {
    public SlideLoadState(boolean isLoading, boolean isLoadError) {
        this.isLoading = isLoading;
        this.isLoadError = isLoadError;
    }


    public SlideLoadState() {
        this.isLoading = false;
        this.isLoadError = false;
    }

    public boolean isLoadError() {
        return isLoadError;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private boolean isLoading;
    private boolean isLoadError;
}
