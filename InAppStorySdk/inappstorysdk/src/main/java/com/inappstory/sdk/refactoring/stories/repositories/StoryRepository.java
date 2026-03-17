package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.core.api.IASStatisticProfiling;
import com.inappstory.sdk.refactoring.core.utils.results.Error;
import com.inappstory.sdk.refactoring.core.utils.results.NoSessionError;
import com.inappstory.sdk.refactoring.core.utils.results.Result;
import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.core.utils.results.Success;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.data.mappers.NStoryCoverToStoryCoverDTOMapper;
import com.inappstory.sdk.refactoring.stories.data.mappers.NStoryToStoryCoverDTOMapper;
import com.inappstory.sdk.refactoring.stories.data.mappers.NStoryToStoryDTOMapper;
import com.inappstory.sdk.refactoring.stories.data.mappers.NStoryToStoryListItemDTOMapper;
import com.inappstory.sdk.refactoring.stories.data.mappers.StoryListItemDTOToStoryCoverDTOMapper;
import com.inappstory.sdk.refactoring.stories.data.network.NFeed;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;
import com.inappstory.sdk.refactoring.stories.data.network.NStoryCover;
import com.inappstory.sdk.refactoring.stories.repositories.datasources.IStoryAPIDataSource;
import com.inappstory.sdk.refactoring.stories.repositories.datasources.IStoryLocalDataSource;
import com.inappstory.sdk.refactoring.stories.usecases.StoriesFeedParameters;

import java.util.ArrayList;
import java.util.List;

public class StoryRepository implements IStoryRepository {

    private final IStoryLocalDataSource storyLocalDataSource;
    private IStoryAPIDataSource storyAPIDataSource;
    private final IASStatisticProfiling profiling;
    private final IStoryChangesSubscribersHolder changesSubscribersHolder;

    public StoryRepository(
            IStoryLocalDataSource localDataSource,
            IStoryAPIDataSource apiDataSource,
            IASStatisticProfiling profiling,
            IStoryChangesSubscribersHolder changesSubscribersHolder
    ) {
        this.changesSubscribersHolder = changesSubscribersHolder;
        this.storyAPIDataSource = apiDataSource;
        this.profiling = profiling;
        this.storyLocalDataSource = localDataSource;
    }


    @Override
    public void updateApiDataSource(IStoryAPIDataSource storyAPIDataSource) {
        this.storyAPIDataSource = storyAPIDataSource;
    }

    @Override
    public void getStoriesFeed(
            StoriesFeedParameters feedParameters,
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
                    List<StoriesListItemDTO> updatedListItems = new ArrayList<>();
                    boolean updateFavoriteCell = false;
                    for (NStory story : feed.stories) {
                        String storyId = Integer.toString(story.id);
                        if (feedDTO.storiesIds.contains(storyId)) continue;
                        feedDTO.storiesIds.add(storyId);
                        StoriesListItemDTO storiesListItemDTO = new NStoryToStoryListItemDTOMapper().convert(story);
                        if (storyLocalDataSource.addOrUpdateStoryListItem(storiesListItemDTO)) {
                            updatedListItems.add(storiesListItemDTO);
                        }
                        if (storiesListItemDTO.favorite()) {
                            updateFavoriteCell |= storyLocalDataSource.addOrUpdateStoryCover(
                                    new NStoryToStoryCoverDTOMapper().convert(story)
                            );
                        }
                    }
                    feedDTO.hasFavorite = feed.hasFavorite();
                    storyLocalDataSource.addOrUpdateStoriesFeed(feedParameters, feedDTO);

                    for (StoriesListItemDTO listItem : updatedListItems) {
                        changesSubscribersHolder.notifyStoryListItemChange(listItem);
                    }
                    if (updateFavoriteCell)
                        changesSubscribersHolder.notifyFavoriteCellChanges(
                                storyLocalDataSource.getFavoriteCovers()
                        );
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
    public void getFavoriteStories(ResultCallback<List<StoriesListItemDTO>> storyFeedResultCallback) {
        if (storyAPIDataSource == null) {
            storyFeedResultCallback.error(new NoSessionError<>());
            return;
        }
        storyAPIDataSource.getFavoriteStories(new ResultCallback<List<NStory>>() {
            @Override
            public void success(List<NStory> items) {
                List<StoriesListItemDTO> stories = new ArrayList<>();
                List<StoriesListItemDTO> updatedListItems = new ArrayList<>();
                boolean updateFavoriteCell = false;
                for (NStory item : items) {
                    StoriesListItemDTO storiesListItemDTO = new NStoryToStoryListItemDTOMapper().convert(item);
                    StoryCoverDTO storyCoverDTO = new NStoryToStoryCoverDTOMapper().convert(item);
                    stories.add(storiesListItemDTO);
                    if (storyLocalDataSource.addOrUpdateStoryListItem(storiesListItemDTO)) {
                        updatedListItems.add(storiesListItemDTO);
                    }
                    updateFavoriteCell |= storyLocalDataSource.addOrUpdateStoryCover(
                            storyCoverDTO
                    );
                }
                for (StoriesListItemDTO listItem : updatedListItems) {
                    changesSubscribersHolder.notifyStoryListItemChange(listItem);
                }
                if (updateFavoriteCell)
                    changesSubscribersHolder.notifyFavoriteCellChanges(
                            storyLocalDataSource.getFavoriteCovers()
                    );
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
        if (storyAPIDataSource == null) {
            storyFeedResultCallback.error(new NoSessionError<>());
            return;
        }
        storyAPIDataSource.getFavoriteCovers(new ResultCallback<List<NStoryCover>>() {
            @Override
            public void success(List<NStoryCover> items) {
                List<StoryCoverDTO> coverDTOs = new ArrayList<>();
                for (NStoryCover storyCover : items) {
                    coverDTOs.add(
                            new NStoryCoverToStoryCoverDTOMapper().convert(storyCover)
                    );
                }
                storyLocalDataSource.setStoryCovers(coverDTOs);
                changesSubscribersHolder.notifyFavoriteCellChanges(
                        storyLocalDataSource.getFavoriteCovers()
                );
            }
        });
    }

    @Override
    public void getOnboardingStoriesFeed(StoriesFeedParameters feedParameters, int limit, ResultCallback<StoryFeedDTO> storyFeedResultCallback) {
        if (storyAPIDataSource == null) {
            storyFeedResultCallback.error(new NoSessionError<>());
            return;
        }
        ResultCallback<NFeed> resultCallback = new ResultCallback<NFeed>() {
            @Override
            public void success(NFeed feed) {
                if (feed.stories == null) {
                    storyFeedResultCallback.error(new Error<>("Can't retrieve stories in feed " + feedParameters.feed()));
                } else {
                    StoryFeedDTO feedDTO = new StoryFeedDTO();
                    List<StoriesListItemDTO> updatedListItems = new ArrayList<>();
                    boolean updateFavoriteCell = false;
                    for (NStory story : feed.stories) {
                        String storyId = Integer.toString(story.id);
                        if (feedDTO.storiesIds.contains(storyId)) continue;
                        feedDTO.storiesIds.add(storyId);
                        StoriesListItemDTO storiesListItemDTO = new NStoryToStoryListItemDTOMapper().convert(story);
                        if (storyLocalDataSource.addOrUpdateStoryListItem(storiesListItemDTO)) {
                            updatedListItems.add(storiesListItemDTO);
                        }
                        if (storiesListItemDTO.favorite()) {
                            updateFavoriteCell |= storyLocalDataSource.addOrUpdateStoryCover(
                                    new NStoryToStoryCoverDTOMapper().convert(story)
                            );
                        }
                    }
                    feedDTO.hasFavorite = feed.hasFavorite();
                    storyLocalDataSource.addOrUpdateStoriesFeed(feedParameters, feedDTO);

                    for (StoriesListItemDTO listItem : updatedListItems) {
                        changesSubscribersHolder.notifyStoryListItemChange(listItem);
                    }
                    if (updateFavoriteCell)
                        changesSubscribersHolder.notifyFavoriteCellChanges(
                                storyLocalDataSource.getFavoriteCovers()
                        );
                    storyFeedResultCallback.success(feedDTO);
                }
            }

            @Override
            public void error(Error<NFeed> result) {
                storyFeedResultCallback.error(new Error<>("Can't retrieve onboarding feed " + feedParameters.feed()));
            }
        };
        storyAPIDataSource.getOnboardingStoriesFeed(feedParameters, limit, resultCallback);
    }

    @Override
    public void likeStory(String storyId, boolean like, ResultCallback<Boolean> likeResultCallback) {
        if (storyAPIDataSource == null) {
            likeResultCallback.error(new NoSessionError<>());
            return;
        }
        storyAPIDataSource.likeStory(storyId, like, new ResultCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                storyLocalDataSource.likeDislikeStory(storyId, like ? 1 : 0);
                likeResultCallback.success(like);
            }

            @Override
            public void error(Error<Boolean> result) {
                likeResultCallback.error(new Error<>("Can't change like status for story: " + storyId));
            }
        });
    }

    @Override
    public void dislikeStory(String storyId, boolean dislike, ResultCallback<Boolean> dislikeResultCallback) {
        if (storyAPIDataSource == null) {
            dislikeResultCallback.error(new NoSessionError<>());
            return;
        }
        storyAPIDataSource.dislikeStory(storyId, dislike, new ResultCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                storyLocalDataSource.likeDislikeStory(storyId, dislike ? -1 : 0);
                dislikeResultCallback.success(dislike);
            }

            @Override
            public void error(Error<Boolean> result) {
                dislikeResultCallback.error(new Error<>("Can't change dislike status for story: " + storyId));
            }
        });
    }

    @Override
    public void favoriteStory(String storyId, boolean favorite, ResultCallback<Boolean> favoriteResultCallback) {
        if (storyAPIDataSource == null) {
            favoriteResultCallback.error(new NoSessionError<>());
            return;
        }
        storyAPIDataSource.favoriteStory(storyId, favorite, new ResultCallback<Boolean>() {
            @Override
            public void success(Boolean res) {
                changesSubscribersHolder.notifyFavoriteFeedChanges(storyId, favorite);
                if (favorite) {
                    Result<StoriesListItemDTO> itemResult = storyLocalDataSource.getStoryListItemById(storyId);
                    if (itemResult instanceof Success) {
                        StoryCoverDTO storyCoverDTO =
                                new StoryListItemDTOToStoryCoverDTOMapper().convert(
                                        ((Success<StoriesListItemDTO>) itemResult).data()
                                );
                        if (storyLocalDataSource.addOrUpdateStoryCover(storyCoverDTO)) {
                            changesSubscribersHolder.notifyFavoriteCellChanges(
                                    storyLocalDataSource.getFavoriteCovers()
                            );
                        }
                    }
                } else {
                    if (storyLocalDataSource.removeStoryCover(storyId)) {
                        changesSubscribersHolder.notifyFavoriteCellChanges(
                                storyLocalDataSource.getFavoriteCovers()
                        );
                    }
                }
            }

            @Override
            public void error(Error<Boolean> result) {
                favoriteResultCallback.error(new Error<>("Can't change favorite status for story: " + storyId));
            }
        });
    }

    @Override
    public void removeAllFavorites(ResultCallback<Void> removeAllFavoritesCallback) {
        if (storyAPIDataSource == null) {
            removeAllFavoritesCallback.error(new NoSessionError<>());
            return;
        }
        storyAPIDataSource.removeAllFavorites(new ResultCallback<Void>() {
            @Override
            public void success(Void result) {
                storyLocalDataSource.removeAllFavorites();
                changesSubscribersHolder.notifyFavoriteCellChanges(
                        storyLocalDataSource.getFavoriteCovers()
                );
                removeAllFavoritesCallback.success(result);
            }

            @Override
            public void error(Error<Void> result) {
                removeAllFavoritesCallback.error(result);
            }
        });
    }

    @Override
    public void getStoryBySlugOrId(
            String storySlugOrId,
            boolean once,
            ResultCallback<StoryDTO> storyByIdResultCallback
    ) {
        ResultCallback<NStory> resultCallback = new ResultCallback<NStory>() {
            @Override
            public void success(NStory story) {
                StoryDTO storyDTO = new NStoryToStoryDTOMapper().convert(story);
                boolean updateFavoriteCell;
                boolean updateListItem;
                String storyId = Integer.toString(storyDTO.id);
                StoriesListItemDTO storiesListItemDTO = new NStoryToStoryListItemDTOMapper().convert(story);
                storyLocalDataSource.addOrUpdateStory(storyDTO);
                updateListItem = storyLocalDataSource.addOrUpdateStoryListItem(storiesListItemDTO);
                if (story.favorite) {
                    updateFavoriteCell = storyLocalDataSource.addOrUpdateStoryCover(
                            new NStoryToStoryCoverDTOMapper().convert(story)
                    );
                } else {
                    updateFavoriteCell = storyLocalDataSource.removeStoryCover(
                            storyId
                    );
                }
                if (updateFavoriteCell)
                    changesSubscribersHolder.notifyFavoriteCellChanges(
                            storyLocalDataSource.getFavoriteCovers()
                    );
                if (updateListItem)
                    changesSubscribersHolder.notifyStoryListItemChange(storiesListItemDTO);
                storyByIdResultCallback.success(storyDTO);
            }

            @Override
            public void error(Error<NStory> result) {
                storyByIdResultCallback.error(new Error<>("Can't retrieve story with id or slug: " + storySlugOrId));
            }
        };
        Result<StoryDTO> result = storyLocalDataSource.getStoryById(storySlugOrId);
        if (result instanceof Error) {
            if (storyAPIDataSource == null) {
                storyByIdResultCallback.error(new NoSessionError<>());
                return;
            }
            storyAPIDataSource.getStoryBySlugOrId(
                    storySlugOrId,
                    once,
                    resultCallback
            );
        } else if (result instanceof Success) {
            storyByIdResultCallback.success(((Success<StoryDTO>) result).data());
        }
    }

    @Override
    public StoriesListItemDTO getLocalStoryListItem(String storyId) {
        Result<StoriesListItemDTO> result = storyLocalDataSource.getStoryListItemById(storyId);
        if (result instanceof Success) {
            return ((Success<StoriesListItemDTO>) result).data();
        }
        return null;
    }

    @Override
    public void destroy() {
        changesSubscribersHolder.destroy();
        storyLocalDataSource.destroy();
    }
}
