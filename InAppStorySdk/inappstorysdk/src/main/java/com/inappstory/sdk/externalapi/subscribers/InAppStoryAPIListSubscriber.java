package com.inappstory.sdk.externalapi.subscribers;


import com.inappstory.sdk.externalapi.StoryAPIData;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;

import java.util.ArrayList;
import java.util.List;

public abstract class InAppStoryAPIListSubscriber implements
        IAPISubscriber<StoryAPIData>,
        IStoryAPIDataHolder {
    private final String uniqueId;
    public final List<StoryAPIData> storyAPIData = new ArrayList<>();

    public abstract void updateFavoriteItemData(List<FavoriteImage> favorites);

    @Override
    public final String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void openStory(int storyId) {

    }

    @Override
    public final StoryAPIData updateStoryAPIData(Story data, String imagePath, String videoPath) {
        for (StoryAPIData storyAPIDataItem : storyAPIData) {
            if (storyAPIDataItem.id == data.id) {
                boolean needToUpdate = false;
                if (data.isOpened != storyAPIDataItem.opened) {
                    storyAPIDataItem.opened = data.isOpened;
                    needToUpdate = true;
                }
                if (storyAPIDataItem.imageFilePath == null && imagePath != null) {
                    storyAPIDataItem.imageFilePath = imagePath;
                    needToUpdate = true;
                }
                if (storyAPIDataItem.videoFilePath == null && videoPath != null) {
                    storyAPIDataItem.videoFilePath = videoPath;
                    needToUpdate = true;
                }
                if (needToUpdate) return storyAPIDataItem;
            }
        }
        return null;
    }

    @Override
    public final void setStoryAPIData(List<StoryAPIData> data) {
        storyAPIData.clear();
        if (data != null)
            storyAPIData.addAll(data);
    }

    @Override
    public void openReader() {

    }

    @Override
    public void closeReader() {

    }

    public InAppStoryAPIListSubscriber(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
