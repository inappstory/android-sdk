package com.inappstory.sdk.stories.uidomain.list.readerconnector;

import com.inappstory.sdk.stories.ui.list.StoriesList;

public interface IStoriesListNotify {
    void unsubscribe();
    void subscribe();

    void bindList(StoriesList list);

    void changeStory(final int storyId, final String listID);

    void closeReader();

    void openReader();

    void changeUserId();

    void clearAllFavorites();

    void storyFavorite(final int id, final boolean favStatus, final boolean isEmpty);
}
