package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.storieslist.StoriesRequestResult;

public interface IStoriesListVMHolder {
    StoriesRequestResult getStoriesRequestResult(StoriesRequestKey key);
    void setStoriesRequestResult(StoriesRequestKey key, StoriesRequestResult state);
    void removeResultById(String cacheId);
    void removeResultByFeed(String feed);
    void removeResultByIdAndFeed(String cacheId, String feed);
    void removeFavoriteResult();

    void clear();
}
