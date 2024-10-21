package com.inappstory.sdk.externalapi.subscribers;

import com.inappstory.sdk.core.dataholders.IListItemContent;
import com.inappstory.sdk.externalapi.StoryAPIData;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.List;

public interface IStoryAPIDataHolder {

    void setStoryAPIData(List<StoryAPIData> data);

    StoryAPIData updateStoryAPIData(IListItemContent data, String imagePath, String videoPath);

    void clear();

    List<StoryAPIData> getStoryAPIData();
}
