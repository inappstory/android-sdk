package com.inappstory.sdk.stories.ui.views;

public interface IProgressLoader {
    void setProgress(int progress, int max);

    void setIndeterminate(boolean indeterminate);
}
