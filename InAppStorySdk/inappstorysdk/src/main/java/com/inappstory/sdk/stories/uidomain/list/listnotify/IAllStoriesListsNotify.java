package com.inappstory.sdk.stories.uidomain.list.listnotify;

import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.ui.list.IStoriesListAdapter;

public interface IAllStoriesListsNotify {
    void unsubscribe();

    void subscribe();

    void bindListAdapter(IStoriesListAdapter storiesListAdapter, int coverQuality);

    void openStory(
            final int storyId,
            StoryType storyType
    );

    void refreshList();

    void clearAllFavorites();

    void storyFavorite(
            final int id,
            StoryType storyType,
            final boolean favStatus,
            final boolean isEmpty
    );
}
