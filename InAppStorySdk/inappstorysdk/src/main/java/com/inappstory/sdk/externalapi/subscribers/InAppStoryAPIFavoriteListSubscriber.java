package com.inappstory.sdk.externalapi.subscribers;


import com.inappstory.sdk.externalapi.StoryAPIData;

import java.util.List;

public abstract class InAppStoryAPIFavoriteListSubscriber implements IAPISubscriber<StoryAPIData> {
    private final String uniqueId;

    @Override
    public final String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void openStory(int storyId) {}

    @Override
    public void updateStoryData(StoryAPIData story) {

    }

    @Override
    public void updateStoriesData(List<StoryAPIData> stories) {

    }

    @Override
    public void openReader() {

    }
    @Override
    public void closeReader() {

    }

    public InAppStoryAPIFavoriteListSubscriber(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
