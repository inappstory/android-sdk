package com.inappstory.sdk.refactoring.stories.repositories.datasources;

import androidx.annotation.NonNull;

import com.inappstory.sdk.refactoring.core.utils.models.Result;
import com.inappstory.sdk.refactoring.stories.IStoryChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;
import com.inappstory.sdk.refactoring.stories.usecases.StoryFeedParameters;

import java.util.List;

public interface IStoryLocalDataSource {
    Result<StoryFeedDTO> getStoriesFeed(@NonNull StoryFeedParameters feedParameters);

    boolean addOrUpdateStoryCover(@NonNull StoryCoverDTO storyCover);

    boolean addOrUpdateStory(@NonNull StoryDTO story);

    void addOrUpdateStoryListItems(@NonNull List<StoryListItemDTO> story);

    boolean updateFavoriteCovers(@NonNull List<StoryListItemDTO> story);

    boolean addOrUpdateStoryListItem(@NonNull StoryListItemDTO story);

    boolean addOrUpdateStoriesFeed(@NonNull StoryFeedParameters feedParameters, @NonNull StoryFeedDTO feed);

    Result<List<StoryCoverDTO>> getFavoriteCovers();

    boolean likeDislikeStory(
            @NonNull String storyId,
            int likeValue
    );

    boolean addStoryToFavorite(@NonNull String storyId);

    boolean removeStoryFromFavorite(@NonNull String storyId);

    void removeAllFavorites();

    Result<StoryDTO> getStoryById(@NonNull String storySlugOrId);

    void addStoryChangeSubscriber(@NonNull IStoryChangeSubscriber subscriber);

    void removeStoryChangeSubscriber(@NonNull IStoryChangeSubscriber subscriber);
}
