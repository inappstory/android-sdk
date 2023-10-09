package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public interface IStoriesAdapterStoryDataStorage {
    void setCurrentStories(List<StoriesAdapterStoryData> stories);

    void notify(StoriesAdapterStoryData data);

    List<StoriesAdapterStoryData> getCurrentStories();
}
