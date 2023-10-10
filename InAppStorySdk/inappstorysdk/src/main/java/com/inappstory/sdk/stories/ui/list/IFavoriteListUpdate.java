package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

public interface IFavoriteListUpdate {
    void favorite(StoriesAdapterStoryData data);
    void removeFromFavorite(StoriesAdapterStoryData data);
}
