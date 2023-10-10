package com.inappstory.sdk.stories.uidomain.list.listnotify;

import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.ui.list.IStoriesListAdapter;

public interface IStoriesListNotify {
    void unsubscribe();

    String getListUID();

    void subscribe();

    void bindListAdapter(IStoriesListAdapter storiesListAdapter);

    void changeStory(
            final int storyId,
            StoryType storyType
    );

    void closeReader();

    void openReader();
}
