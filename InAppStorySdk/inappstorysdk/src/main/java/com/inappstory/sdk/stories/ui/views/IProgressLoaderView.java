package com.inappstory.sdk.stories.ui.views;

public interface IProgressLoaderView {
    void setProgress(int progress, int max);

    void setIndeterminate(boolean indeterminate);
}
