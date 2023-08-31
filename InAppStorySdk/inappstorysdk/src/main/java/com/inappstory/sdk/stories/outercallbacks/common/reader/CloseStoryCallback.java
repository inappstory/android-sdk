package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface CloseStoryCallback {

    void closeStory(
            SlideData slideData,
            CloseReader action
    );
}
