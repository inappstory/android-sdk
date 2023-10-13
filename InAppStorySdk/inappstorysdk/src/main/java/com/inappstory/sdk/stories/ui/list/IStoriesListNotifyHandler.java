package com.inappstory.sdk.stories.ui.list;

public interface IStoriesListNotifyHandler {
    void changeStory(final int storyId);

    void scrollToLastOpenedStory();

    void closeReader();

    void openReader();
}
