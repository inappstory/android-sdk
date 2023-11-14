package com.inappstory.sdk.stories.uidomain.list;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.screen.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.screen.IOpenStoriesReader;
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

    void gameItemClick(
            IPreviewStoryDTO data,
            int index,
            Context context,
            IOpenGameReader callback
    );

    void deeplinkItemClick(
            IPreviewStoryDTO data,
            int index,
            Context context
    );

    void commonItemClick(
            List<IPreviewStoryDTO> data,
            int index,
            Context context,
            IOpenStoriesReader callback
    );

    void loadFeed(String feed, boolean loadFavoriteCovers, GetStoriesList getStoriesList);

    void loadFavoriteList(GetStoriesList getStoriesList);

    void sendPreviewsToStatistic(List<Integer> indexes, String feed, boolean isFavoriteList);
}
