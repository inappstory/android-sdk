package com.inappstory.sdk.stories.uidomain.list.items.story;

import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public interface IStoriesListCommonItemClick {
    void onClick(List<StoriesAdapterStoryData> storiesData, int index);
}
