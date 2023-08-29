package com.inappstory.sdk.stories.outercallbacks.storieslist;

import com.inappstory.sdk.stories.ui.list.StoriesListItemData;

import java.util.List;

public interface ListScrollCallback {
    void scrollStart();

    void onVisibleAreaUpdated(List<StoriesListItemData> storiesListItemData, String feed, boolean isFavoriteList);

    void scrollEnd();
}
