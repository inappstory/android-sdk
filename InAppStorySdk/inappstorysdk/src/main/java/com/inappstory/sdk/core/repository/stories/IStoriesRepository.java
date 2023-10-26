package com.inappstory.sdk.core.repository.stories;

import java.util.List;

public interface IStoriesRepository {
    void getStoryById(int storyId);

    void getStoriesByIds(List<Integer> ids);

    void getStoriesByFeedId();

    void getOnboardingStories(String userId);
}
