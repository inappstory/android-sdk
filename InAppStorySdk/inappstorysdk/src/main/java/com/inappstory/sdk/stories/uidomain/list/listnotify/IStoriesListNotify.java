package com.inappstory.sdk.stories.uidomain.list.listnotify;

import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.ui.list.IStoriesListNotifyHandler;

public interface IStoriesListNotify {
    void unsubscribe();

    String getListUID();

    void subscribe();

    void bindList(IStoriesListNotifyHandler storiesListNotifyHandler);

    void changeStory(
            final int storyId,
            StoryType storyType
    );

    void scrollToLastOpenedStory();

    void closeReader();

    void openReader();
}
