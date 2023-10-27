package com.inappstory.sdk.core.repository.stories;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoriesPreviewsCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoryCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IStoryUpdatedCallback;

public interface IStoriesRepository {
    void getStoryPreviewById(int storyId, IGetStoryCallback<IPreviewStoryDTO> callback);
    void getStoryById(int storyId, IGetStoryCallback<IStoryDTO> callback);
    void removeCachedList(String listID);
    void removeCachedLists();
    void getStoriesPreviewsByListId(String listID, String feed, IGetStoriesPreviewsCallback callback);
    void getStoriesPreviewsFavoriteList(String listID);
    void getStoriesPreviewsFavoriteItem();
    void getOnboardingStories(String userId);
    void openStory(int storyId);

    void addStoryUpdateCallback(IStoryUpdatedCallback callback);

    void removeStoryUpdateCallback(IStoryUpdatedCallback callback);
}
