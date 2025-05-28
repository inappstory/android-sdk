package com.inappstory.sdk.externalapi.subscribers;

import com.inappstory.sdk.externalapi.StoryAPIData;
import com.inappstory.sdk.externalapi.storylist.IASStoryListSessionData;

import java.util.List;

public abstract class InAppStoryAPIStackFeedSubscriber implements IAPISubscriber<StoryAPIData> {
    private final String uniqueId;

    @Override
    public final String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void updateStoryData(StoryAPIData story, IASStoryListSessionData sessionData) {

    }


    @Override
    public void updateStoriesData(List<StoryAPIData> stories, IASStoryListSessionData sessionData) {

    }

    @Override
    public void readerIsOpened() {

    }
    @Override
    public void readerIsClosed() {

    }

    public InAppStoryAPIStackFeedSubscriber(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
