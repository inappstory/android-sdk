package com.inappstory.sdk.stories.ui.list;

public interface ListManager {
    void clear();

    void changeStory(final int storyId, final String listID);

    void readerIsClosed();

    void readerIsOpened();

    void userIdChanged();

    void sessionIsOpened(String currentSessionId);

    void clearAllFavorites();

    void storyFavorite(final int id, final boolean favStatus, final boolean isEmpty);
}
