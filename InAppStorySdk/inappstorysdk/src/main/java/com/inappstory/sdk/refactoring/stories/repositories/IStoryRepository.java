package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.usecases.StoriesFeedParameters;

import java.util.List;

public interface IStoryRepository {
    void getStoriesFeed(
            StoriesFeedParameters feedParameters,
            boolean useLocal,
            ResultCallback<StoryFeedDTO> storyFeedResultCallback
    );

    void getFavoriteStories(
            ResultCallback<List<StoriesListItemDTO>> storyFeedResultCallback
    );

    void getFavoriteCovers(
            ResultCallback<List<StoryCoverDTO>> storyFeedResultCallback
    );

    void getOnboardingStoriesFeed(
            StoriesFeedParameters feedParameters,
            int limit,
            ResultCallback<StoryFeedDTO> storyFeedResultCallback
    );

    void likeStory(
            String storyId,
            boolean like,
            ResultCallback<Boolean> likeResultCallback
    );

    void dislikeStory(
            String storyId,
            boolean dislike,
            ResultCallback<Boolean> dislikeResultCallback
    );

    void favoriteStory(
            String storyId,
            boolean favorite,
            ResultCallback<Boolean> favoriteResultCallback
    );

    void removeAllFavorites(
            ResultCallback<Void> removeAllFavoritesCallback
    );

    void getStoryBySlugOrId(
            String storySlugOrId,
            boolean once,
            ResultCallback<StoryDTO> storyByIdResultCallback
    );

    void destroy();
}
