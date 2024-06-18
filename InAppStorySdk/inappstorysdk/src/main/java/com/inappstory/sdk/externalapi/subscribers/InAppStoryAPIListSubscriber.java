package com.inappstory.sdk.externalapi.subscribers;


import androidx.annotation.WorkerThread;

import com.inappstory.sdk.externalapi.StoryAPIData;
import com.inappstory.sdk.externalapi.StoryFavoriteItemAPIData;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class InAppStoryAPIListSubscriber implements
        IAPISubscriber<StoryAPIData>,
        IStoryAPIDataHolder {
    private final String uniqueId;

    public final List<StoryAPIData> getStoryAPIData() {
        return storyAPIData;
    }

    private final List<StoryAPIData> storyAPIData = new ArrayList<>();


    @WorkerThread
    public abstract void updateFavoriteItemData(List<StoryFavoriteItemAPIData> favorites);

    @Override
    public final String getUniqueId() {
        return uniqueId;
    }

    @Override
    @WorkerThread
    public void storyIsOpened(int storyId) {

    }

    @Override
    public final void clear() {

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
    @WorkerThread
    public void readerIsOpened() {

    }

    @Override
    @WorkerThread
    public void readerIsClosed() {

    }

    public InAppStoryAPIListSubscriber(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
