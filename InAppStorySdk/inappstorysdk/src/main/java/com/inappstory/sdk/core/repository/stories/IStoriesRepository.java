package com.inappstory.sdk.core.repository.stories;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoriesPreviewsCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoryCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IStoryUpdatedCallback;

import java.util.List;

public interface IStoriesRepository extends IFavoriteStoriesManager, ILikeDislikeStoriesManager {

    void getStoryByIdAsync(int storyId, IGetStoryCallback<IStoryDTO> callback);

    IStoryDTO getStoryById(int storyId);

    IPreviewStoryDTO getCurrentStory();

    void setCurrentStory(Integer storyId);

    void getStoryByStringId(String storyId, IGetStoryCallback<IStoryDTO> callback);

    void clearCachedList(String listID);

    void clearCachedLists();

    void getStoriesPreviewsByListIdAsync(
            String listID,
            String uncheckedFeed,
            boolean loadFavorites,
            IGetStoriesPreviewsCallback callback
    );

    int getStoryLastIndex(int storyId);

    void setStoryLastIndex(int storyId, int index);

    void clearStoriesIndexes();

    void setOpenedStories(List<Integer> ids);

    void getOpenedStories(List<Integer> ids);

    void getOnboardingStoriesAsync(
            String uncheckedFeed,
            Integer limit,
            IGetStoriesPreviewsCallback callback
    );

    void getFavoriteStoriesByListIdAsync(
            String listID,
            IGetStoriesPreviewsCallback callback
    );

    IPreviewStoryDTO getStoryPreviewById(int storyId);

    void openStory(int storyId);

    void addStoryUpdateCallback(IStoryUpdatedCallback callback);

    void removeStoryUpdateCallback(IStoryUpdatedCallback callback);

    void addStoryUpdatedCallback(IStoryUpdatedCallback callback);

    void removeStoryUpdatedCallback(IStoryUpdatedCallback callback);
}
