package com.inappstory.sdk.stories.outercallbacks.storieslist;

import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;

import java.util.List;

public interface ListScrollCallback {
    void scrollStart();

    void onVisibleAreaUpdated(List<ShownStoriesListItem> shownStoriesListItemData, String feed, boolean isFavoriteList);

    void scrollEnd();
}
