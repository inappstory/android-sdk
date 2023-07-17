package com.inappstory.sdk.stories.ui.views;

import android.view.View;

public interface IGameLoaderView {
    View getView();
    void setProgress(int progress, int max);

    void setIndeterminate(boolean indeterminate);
}
