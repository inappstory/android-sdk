package com.inappstory.sdk.refactoring.stories.repositories.datasources;

import androidx.annotation.NonNull;

import com.inappstory.sdk.refactoring.core.utils.models.Result;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;
import com.inappstory.sdk.refactoring.stories.usecases.StoryFeedParameters;

import java.util.List;

public interface IStoryLocalDataSource {
    Result<StoryFeedDTO> getStoriesFeed(@NonNull StoryFeedParameters feedParameters);

    boolean addOrUpdateStoryCover(@NonNull StoryCoverDTO storyCover);

    void setStoryCovers(@NonNull List<StoryCoverDTO> storyCovers);

    boolean removeStoryCover(@NonNull String storyId);

    List<StoryCoverDTO> getFavoriteCovers();

    boolean addOrUpdateStory(@NonNull StoryDTO story);

    boolean addOrUpdateStoryListItem(@NonNull StoryListItemDTO story);

    boolean addOrUpdateStoriesFeed(@NonNull StoryFeedParameters feedParameters, @NonNull StoryFeedDTO feed);

    boolean likeDislikeStory(
            @NonNull String storyId,
            int likeValue
    );

    void removeAllFavorites();

    Result<StoryDTO> getStoryById(@NonNull String storySlugOrId);

    Result<StoryListItemDTO> getStoryListItemById(@NonNull String storyId);

    void destroy();
}
