package com.inappstory.sdk.stories.uidomain.list;

import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.utils.GetStoriesListIds;

import java.util.List;

public interface IStoriesListPresenter {
    ShownStoriesListItem getShownStoriesListItemByStoryId(
            int storyId,
            int listIndex,
            float currentPercentage,
            String feed,
            SourceType sourceType
    );

    void setCacheId(String cacheId);

    void clearCachedList();

    void onWindowFocusChanged();

    boolean hasUgcEditor();

    void loadFeed(String feed, boolean loadFavoriteCovers, GetStoriesListIds getStoriesListIds);

    void loadFavoriteList(GetStoriesListIds getStoriesListIds);

    void sendPreviewsToStatistic(List<Integer> indexes, String feed, boolean isFavoriteList);
}
