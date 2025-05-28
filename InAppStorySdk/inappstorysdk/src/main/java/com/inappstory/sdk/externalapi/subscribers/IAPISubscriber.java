package com.inappstory.sdk.externalapi.subscribers;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.externalapi.storylist.IASStoryListSessionData;

import java.util.List;

public interface IAPISubscriber<T> {

    @WorkerThread
    void storyIsOpened(int storyId);

    @WorkerThread
    void updateStoryData(T story, IASStoryListSessionData sessionData);

    @WorkerThread
    void updateStoriesData(List<T> stories, IASStoryListSessionData sessionData);

    @WorkerThread
    void readerIsOpened();

    @WorkerThread
    void readerIsClosed();

    String getUniqueId();
}
