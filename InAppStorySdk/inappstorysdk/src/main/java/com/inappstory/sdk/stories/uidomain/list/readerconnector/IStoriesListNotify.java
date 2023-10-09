package com.inappstory.sdk.stories.uidomain.list.readerconnector;

import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.ui.list.IStoriesListAdapter;

public interface IStoriesListNotify {
    void unsubscribe();

    void subscribe();

    void bindListAdapter(IStoriesListAdapter storiesListAdapter, int coverQuality);

    void changeStory(
            final int storyId,
            StoryType storyType,
            final String listID
    );

    void openStory(
            final int storyId,
            StoryType storyType,
            final String listID
    );

    void closeReader();

    void openReader();

    void changeUserId();

    void clearAllFavorites();

    void storyFavorite(
            final int id,
            StoryType storyType,
            final boolean favStatus,
            final boolean isEmpty
    );
}
