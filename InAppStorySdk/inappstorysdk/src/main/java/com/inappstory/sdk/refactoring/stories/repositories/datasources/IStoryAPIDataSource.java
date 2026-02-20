package com.inappstory.sdk.refactoring.stories.repositories.datasources;

import com.inappstory.sdk.refactoring.core.utils.models.Result;
import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.network.NFeed;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;
import com.inappstory.sdk.refactoring.stories.data.network.NStoryCover;
import com.inappstory.sdk.refactoring.stories.usecases.StoryFeedParameters;

import java.util.List;

public interface IStoryAPIDataSource {
    void getStoriesFeed(
            StoryFeedParameters feedParameters,
            ResultCallback<NFeed> storyFeedResultCallback
    );

    void getFavoriteStories(
            ResultCallback<List<NStory>> storyFeedResultCallback
    );

    void getFavoriteCovers(
            ResultCallback<List<NStoryCover>> storyFeedResultCallback
    );

    void getOnboardingStoriesFeed(
            StoryFeedParameters feedParameters,
            int limit,
            ResultCallback<NFeed> storyFeedResultCallback
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

    Result<NStory> getStoryBySlugOrId(
            String storySlugOrId
    );
}
