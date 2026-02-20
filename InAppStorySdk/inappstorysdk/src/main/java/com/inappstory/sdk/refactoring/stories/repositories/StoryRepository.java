package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.core.utils.models.Error;
import com.inappstory.sdk.refactoring.core.utils.models.Result;
import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.core.utils.models.Success;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;
import com.inappstory.sdk.refactoring.stories.data.mappers.NStoryCoverToStoryCoverDTOMapper;
import com.inappstory.sdk.refactoring.stories.data.mappers.NStoryToStoryCoverDTOMapper;
import com.inappstory.sdk.refactoring.stories.data.mappers.NStoryToStoryListItemDTOMapper;
import com.inappstory.sdk.refactoring.stories.data.network.NFeed;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;
import com.inappstory.sdk.refactoring.stories.data.network.NStoryCover;
import com.inappstory.sdk.refactoring.stories.repositories.datasources.IStoryAPIDataSource;
import com.inappstory.sdk.refactoring.stories.repositories.datasources.IStoryLocalDataSource;
import com.inappstory.sdk.refactoring.stories.usecases.StoryFeedParameters;

import java.util.ArrayList;
import java.util.List;

public class StoryRepository implements IStoryRepository {

    private final IStoryLocalDataSource storyLocalDataSource;
    private final IStoryAPIDataSource storyAPIDataSource;

    public StoryRepository(IStoryLocalDataSource localDataSource, IStoryAPIDataSource apiDataSource) {
        this.storyAPIDataSource = apiDataSource;
        this.storyLocalDataSource = localDataSource;
    }

    @Override
    public void getStoriesFeed(
            StoryFeedParameters feedParameters,
            boolean useLocal,
            final ResultCallback<StoryFeedDTO> storyFeedResultCallback
    ) {
        ResultCallback<NFeed> resultCallback = new ResultCallback<NFeed>() {
            @Override
            public void success(NFeed feed) {
                if (feed.stories == null) {
                    storyFeedResultCallback.error(new Error<>("Can't retrieve stories in feed " + feedParameters.feed()));
                } else {
                    StoryFeedDTO feedDTO = new StoryFeedDTO();
                    List<StoryListItemDTO> listItems = new ArrayList<>();
                    for (NStory story : feed.stories) {
                        String storyId = Integer.toString(story.id);
                        if (feedDTO.storiesIds.contains(storyId)) continue;
                        feedDTO.storiesIds.add(storyId);
                        listItems.add(new NStoryToStoryListItemDTOMapper().convert(story));
                    }
                    feedDTO.hasFavorite = feed.hasFavorite();
                    storyLocalDataSource.addOrUpdateStoryListItems(listItems);
                    storyLocalDataSource.addOrUpdateStoriesFeed(feedParameters, feedDTO);
                    storyFeedResultCallback.success(feedDTO);
                }
            }

            @Override
            public void error(Error<NFeed> result) {
                storyFeedResultCallback.error(new Error<>("Can't retrieve feed " + feedParameters.feed()));
            }
        };
        if (useLocal) {
            Result<StoryFeedDTO> result = storyLocalDataSource.getStoriesFeed(feedParameters);
            if (result instanceof Error) {
                storyAPIDataSource.getStoriesFeed(feedParameters, resultCallback);
            } else if (result instanceof Success) {
                storyFeedResultCallback.success(((Success<StoryFeedDTO>) result).data());
            }
        } else {
            storyAPIDataSource.getStoriesFeed(feedParameters, resultCallback);
        }
    }

    @Override
    public void getFavoriteStories(ResultCallback<List<StoryListItemDTO>> storyFeedResultCallback) {
        storyAPIDataSource.getFavoriteStories(new ResultCallback<List<NStory>>() {
            @Override
            public void success(List<NStory> items) {
                List<StoryListItemDTO> stories = new ArrayList<>();
                for (NStory item : items) {
                    StoryListItemDTO storyListItemDTO = new NStoryToStoryListItemDTOMapper().convert(item);
                    StoryCoverDTO storyCoverDTO = new NStoryToStoryCoverDTOMapper().convert(item);
                    stories.add(storyListItemDTO);
                    storyLocalDataSource.addOrUpdateStoryListItem(storyListItemDTO);
                    storyLocalDataSource.addOrUpdateStoryCover(storyCoverDTO);
                }
                storyFeedResultCallback.success(stories);
            }

            @Override
            public void error(Error<List<NStory>> result) {
                storyFeedResultCallback.error(new Error<>("Can't retrieve favorite stories"));
            }
        });
    }

    @Override
    public void getFavoriteCovers(ResultCallback<List<StoryCoverDTO>> storyFeedResultCallback) {
        storyAPIDataSource.getFavoriteCovers(new ResultCallback<List<NStoryCover>>() {
            @Override
            public void success(List<NStoryCover> items) {
                for (NStoryCover storyCover : items) {
                    storyLocalDataSource.addOrUpdateStoryCover(
                            new NStoryCoverToStoryCoverDTOMapper().convert(storyCover)
                    );
                }
            }
        });
    }

    @Override
    public void getOnboardingStoriesFeed(StoryFeedParameters feedParameters, ResultCallback<StoryFeedDTO> storyFeedResultCallback) {

    }

    @Override
    public void likeStory(String storyId, boolean like, ResultCallback<Boolean> likeResultCallback) {

    }

    @Override
    public void dislikeStory(String storyId, boolean dislike, ResultCallback<Boolean> dislikeResultCallback) {

    }

    @Override
    public void favoriteStory(String storyId, boolean favorite, ResultCallback<Boolean> favoriteResultCallback) {

    }

    @Override
    public void removeAllFavorites(ResultCallback<Void> removeAllFavoritesCallback) {

    }

    @Override
    public void getStoryBySlugOrId(String storySlugOrId, ResultCallback<StoryDTO> storyByIdResultCallback) {

    }
}
