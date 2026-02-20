package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;
import com.inappstory.sdk.refactoring.stories.usecases.StoryFeedParameters;

import java.util.List;

public interface IStoryRepository {
    void getStoriesFeed(
            StoryFeedParameters feedParameters,
            boolean useLocal,
            ResultCallback<StoryFeedDTO> storyFeedResultCallback
    );

    void getFavoriteStories(
            ResultCallback<List<StoryListItemDTO>> storyFeedResultCallback
    );

    void getFavoriteCovers(
            ResultCallback<List<StoryCoverDTO>> storyFeedResultCallback
    );

    void getOnboardingStoriesFeed(
            StoryFeedParameters feedParameters,
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
            ResultCallback<StoryDTO> storyByIdResultCallback
    );
}
