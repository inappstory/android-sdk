package com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay;

import androidx.lifecycle.LiveData;

public class StoriesWebViewDisplayViewModel implements IStoriesWebViewDisplayViewModel {
    @Override
    public void freezeUI() {

    }

    @Override
    public void unfreezeUI() {

    }

    @Override
    public boolean isUIFrozen() {
        return false;
    }

    @Override
    public LiveData<String> evaluateJSCalls() {
        return null;
    }

    @Override
    public LiveData<String> loadUrlCalls() {
        return null;
    }

    @Override
    public LiveData<SlideContentState> slideContentState() {
        return null;
    }
}
