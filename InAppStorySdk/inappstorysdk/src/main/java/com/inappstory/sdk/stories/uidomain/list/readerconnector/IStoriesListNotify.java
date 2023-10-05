package com.inappstory.sdk.stories.uidomain.list.readerconnector;

public interface IStoriesListNotify {
    void clear();

    void changeStory(final int storyId, final String listID);

    void closeReader();

    void openReader();

    void changeUserId();

    void clearAllFavorites();

    void storyFavorite(final int id, final boolean favStatus, final boolean isEmpty);
}
