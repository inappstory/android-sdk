package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public interface IFavoriteStoriesListAdapter {
    void favStory();

    void notify(StoriesAdapterStoryData data);

    List<StoriesAdapterStoryData> getCurrentStories();
}
