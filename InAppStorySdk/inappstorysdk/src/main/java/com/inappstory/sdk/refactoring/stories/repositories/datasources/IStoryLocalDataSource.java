package com.inappstory.sdk.refactoring.stories.repositories.datasources;

import androidx.annotation.NonNull;

import com.inappstory.sdk.refactoring.core.utils.results.Result;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.usecases.StoriesFeedParameters;

import java.util.List;

public interface IStoryLocalDataSource {
    Result<StoryFeedDTO> getStoriesFeed(@NonNull StoriesFeedParameters feedParameters);

    boolean addOrUpdateStoryCover(@NonNull StoryCoverDTO storyCover);

    void setStoryCovers(@NonNull List<StoryCoverDTO> storyCovers);

    boolean removeStoryCover(@NonNull String storyId);

    List<StoryCoverDTO> getFavoriteCovers();

    boolean addOrUpdateStory(@NonNull StoryDTO story);

    boolean addOrUpdateStoryListItem(@NonNull StoriesListItemDTO story);

    boolean addOrUpdateStoriesFeed(@NonNull StoriesFeedParameters feedParameters, @NonNull StoryFeedDTO feed);

    boolean likeDislikeStory(
            @NonNull String storyId,
            int likeValue
    );

    void removeAllFavorites();

    Result<StoryDTO> getStoryById(@NonNull String storySlugOrId);

    Result<StoriesListItemDTO> getStoryListItemById(@NonNull String storyId);

    void destroy();
}
