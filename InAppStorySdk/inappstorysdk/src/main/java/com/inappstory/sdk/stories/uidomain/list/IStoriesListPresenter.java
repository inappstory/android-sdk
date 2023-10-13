package com.inappstory.sdk.stories.uidomain.list;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.utils.GetStoriesList;

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

    void setListCallback(ListCallback listCallback);

    void clearCachedList();

    void onWindowFocusChanged();

    void updateAppearanceManager(AppearanceManager appearanceManager);

    boolean hasUgcEditor();

    void gameItemClick(StoriesAdapterStoryData data, int index, Context context);

    void deeplinkItemClick(StoriesAdapterStoryData data, int index, Context context);

    void commonItemClick(List<StoriesAdapterStoryData> data, int index, Context context);

    void loadFeed(String feed, boolean loadFavoriteCovers, GetStoriesList getStoriesList);

    void loadFavoriteList(GetStoriesList getStoriesList);

    void sendPreviewsToStatistic(List<Integer> indexes, String feed, boolean isFavoriteList);
}
