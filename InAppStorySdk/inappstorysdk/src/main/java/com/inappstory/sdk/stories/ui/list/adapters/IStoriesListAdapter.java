package com.inappstory.sdk.stories.ui.list.adapters;

import android.view.View;

import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public interface IStoriesListAdapter {

    void refreshList();

    void clearAllFavorites();

    void notify(StoriesAdapterStoryData data);

    BaseStoriesListItem getViewHolderItem(View view, int viewType);

    List<StoriesAdapterStoryData> getCurrentStories();
}
