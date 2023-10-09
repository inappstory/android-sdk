package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public interface IStoriesListAdapter extends IStoriesAdapterStoryDataStorage {
    void favStory(
            StoriesAdapterStoryData data,
            boolean favStatus,
            List<FavoriteImage> favImages,
            boolean isEmpty
    );

    void changeStoryEvent(int storyId);

    void closeReader();

    void openReader();

    void refreshList();

    void clearAllFavorites();
}
