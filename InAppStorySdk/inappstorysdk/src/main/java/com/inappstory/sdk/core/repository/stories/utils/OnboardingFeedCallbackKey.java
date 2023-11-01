package com.inappstory.sdk.core.repository.stories.utils;

import java.util.Objects;

public class OnboardingFeedCallbackKey implements IFeedCallbackKey {
    public OnboardingFeedCallbackKey(String feed) {
        this.feed = feed;
    }

    private
    final String feed;

    @Override
    public int getKey() {
        return Objects.hash(feed, StoriesFeedType.ONBOARDING, StoriesListType.FEED);
    }
}
