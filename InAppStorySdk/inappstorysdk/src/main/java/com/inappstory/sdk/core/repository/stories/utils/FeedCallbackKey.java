package com.inappstory.sdk.core.repository.stories.utils;

import java.util.Objects;

public class FeedCallbackKey implements IFeedCallbackKey {
    public FeedCallbackKey(String feed) {
        this.feed = feed;
    }

    private final String feed;

    @Override
    public int getKey() {
        return Objects.hash(feed, StoriesFeedType.COMMON, StoriesListType.FEED);
    }
}
