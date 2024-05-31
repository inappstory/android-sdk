package com.inappstory.sdk.externalapi.subscribers;

import androidx.annotation.WorkerThread;

import java.util.List;

public interface IAPISubscriber<T> {

    @WorkerThread
    void openStory(int storyId);

    @WorkerThread
    void updateStoryData(T story);

    @WorkerThread
    void updateStoriesData(List<T> stories);

    @WorkerThread
    void openReader();

    @WorkerThread
    void closeReader();

    String getUniqueId();
}
