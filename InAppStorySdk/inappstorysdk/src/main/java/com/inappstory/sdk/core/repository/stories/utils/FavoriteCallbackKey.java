package com.inappstory.sdk.core.repository.stories.utils;

import java.util.Objects;

public class FavoriteCallbackKey implements IFeedCallbackKey {

    @Override
    public int getKey() {
        return Objects.hash(null, StoriesFeedType.COMMON, StoriesListType.FAVORITE);
    }
}
