package com.inappstory.sdk.stories.ui.list;

import android.view.View;

import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public interface IStoriesListAdapter {

    void changeStoryEvent(int storyId);

    void closeReader();

    void openReader();

    void refreshList();

    void clearAllFavorites();

    void openStory();

    BaseStoriesListItem getViewHolderItem(View view, int viewType);

    List<StoriesAdapterStoryData> getCurrentStories();
}
