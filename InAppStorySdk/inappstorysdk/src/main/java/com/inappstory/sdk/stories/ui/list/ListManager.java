package com.inappstory.sdk.stories.ui.list;

public interface ListManager {
    public void clear();

    public void changeStory(final int storyId, final String listID);

    public void closeReader();

    public void openReader();

    public void changeUserId();

    public void clearAllFavorites();

    void storyFavorite(final int id, final boolean favStatus, final boolean isEmpty);
}
