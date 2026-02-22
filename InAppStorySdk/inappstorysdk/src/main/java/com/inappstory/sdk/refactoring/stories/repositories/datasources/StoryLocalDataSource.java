package com.inappstory.sdk.refactoring.stories.repositories.datasources;

import androidx.annotation.NonNull;

import com.inappstory.sdk.refactoring.core.utils.models.Error;
import com.inappstory.sdk.refactoring.core.utils.models.Result;
import com.inappstory.sdk.refactoring.core.utils.models.Success;
import com.inappstory.sdk.refactoring.stories.IStoryChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;
import com.inappstory.sdk.refactoring.stories.data.mappers.StoryListItemDTOToStoryCoverDTOMapper;
import com.inappstory.sdk.refactoring.stories.usecases.StoryFavoriteFeedParameters;
import com.inappstory.sdk.refactoring.stories.usecases.StoryFeedParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class StoryLocalDataSource implements IStoryLocalDataSource {

    private final List<StoryCoverDTO> favoriteCovers = new ArrayList<>();
    private boolean favoriteCoversLoaded = false;
    private final Map<String, StoryListItemDTO> storyListItems = new HashMap<>();
    private final StoryFeedParameters favFeedKey =
            new StoryFavoriteFeedParameters();
    private final Map<String, StoryDTO> stories = new HashMap<>();
    private final Map<StoryFeedParameters, StoryFeedDTO> feeds = new HashMap<>();
    private final Object contentLock = new Object();

    public StoryLocalDataSource() {
        feeds.put(favFeedKey, null);
    }

    @Override
    public Result<StoryFeedDTO> getStoriesFeed(@NonNull StoryFeedParameters feedParameters) {
        synchronized (contentLock) {
            StoryFeedDTO feedDTO = this.feeds.get(feedParameters);
            if (feedDTO == null) return new Error<>("No local feed");
            else return new Success<>(feedDTO);
        }
    }

    @Override
    public boolean addOrUpdateStoryCover(@NonNull StoryCoverDTO storyCover) {
        synchronized (contentLock) {
            if (!favoriteCoversLoaded) return false;
            if (favoriteCovers.contains(storyCover)) return false;
            favoriteCovers.add(storyCover);
        }
        return true;
    }

    @Override
    public boolean removeStoryCover(@NonNull String storyId) {
        if (favoriteCoversLoaded) {
            synchronized (contentLock) {
                Iterator<StoryCoverDTO> coversIterator = favoriteCovers.iterator();
                while (coversIterator.hasNext()) {
                    StoryCoverDTO storyCover = coversIterator.next();
                    if (storyCover != null && Objects.equals(Integer.toString(storyCover.id()), storyId)) {
                        coversIterator.remove();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean addOrUpdateStory(@NonNull StoryDTO story) {
        String key = Integer.toString(story.id());
        synchronized (contentLock) {
            if (Objects.equals(this.stories.get(key), story)) return false;
            this.stories.put(key, story);
        }
        return true;
    }

  /*  private void notifyStoryUpdates(@NonNull StoryDTO story) {
        String storyId = Integer.toString(story.id);
        List<IStoryChangeSubscriber> tempSubscribers = new ArrayList<>();
        synchronized (subscribersLock) {
            if (storyChangeSubscribers.get(storyId) != null) {
                tempSubscribers.addAll(Objects.requireNonNull(storyChangeSubscribers.get(storyId)));
            }
        }
        for (IStoryChangeSubscriber subscriber : tempSubscribers) {
            subscriber.onChange(story);
        }
    }

    private void notifyFeedUpdates(@NonNull StoryFeedParameters feedParameters) {

    }

    private void notifyFavoriteCoverUpdates() {

    }*/

    @Override
    public void addOrUpdateStoryListItems(@NonNull List<StoryListItemDTO> stories) {
        synchronized (contentLock) {
            for (StoryListItemDTO storyListItemDTO : stories) {
                String key = Integer.toString(storyListItemDTO.id());
                storyListItems.put(key, storyListItemDTO);
                StoryDTO storyDTO = this.stories.get(key);
                if (storyDTO != null) {
                    storyDTO.like = storyListItemDTO.like();
                    storyDTO.favorite = storyListItemDTO.favorite();
                }
               /* StoryCoverDTO storyCoverDTO = new StoryListItemDTOToStoryCoverDTOMapper().convert(storyListItemDTO);
                if (storyListItemDTO.favorite() && !favoriteCovers.contains(storyCoverDTO)) {
                    favoriteCovers.add(storyCoverDTO);
                } else if (!storyListItemDTO.favorite() && favoriteCovers.contains(storyCoverDTO)) {
                    favoriteCovers.remove(storyCoverDTO);
                }*/
            }
        }
    }

    @Override
    public boolean updateFavoriteCovers(@NonNull List<StoryListItemDTO> stories) {
        boolean favoriteCoversUpdated = false;
        synchronized (contentLock) {
            favoriteCoversLoaded = true;
            for (StoryListItemDTO storyListItemDTO : stories) {
                StoryCoverDTO storyCoverDTO = new StoryListItemDTOToStoryCoverDTOMapper().convert(storyListItemDTO);
                if (storyListItemDTO.favorite() && !favoriteCovers.contains(storyCoverDTO)) {
                    favoriteCovers.add(storyCoverDTO);
                    favoriteCoversUpdated = true;
                } else if (!storyListItemDTO.favorite() && favoriteCovers.contains(storyCoverDTO)) {
                    favoriteCovers.remove(storyCoverDTO);
                    favoriteCoversUpdated = true;
                }
            }
        }
        return favoriteCoversUpdated;
    }

    @Override
    public boolean addOrUpdateStoryListItem(@NonNull StoryListItemDTO story) {
        String key = Integer.toString(story.id());
        synchronized (contentLock) {
            if (Objects.equals(this.storyListItems.get(key), story)) return false;
            this.storyListItems.put(key, story);
        }
        return true;
    }

    @Override
    public boolean addOrUpdateStoriesFeed(@NonNull StoryFeedParameters feedParameters, @NonNull StoryFeedDTO feed) {
        synchronized (contentLock) {
            if (Objects.equals(this.feeds.get(feedParameters), feed)) return false;
            this.feeds.put(feedParameters, feed);
        }
        return true;
    }

    @Override
    public Result<List<StoryCoverDTO>> getFavoriteCovers() {
        synchronized (contentLock) {
            if (favoriteCoversLoaded)
                return new Success<>(favoriteCovers);
            else
                return new Error<>("Favorite covers not loaded");
        }
    }

    @Override
    public boolean likeDislikeStory(@NonNull String storyId, int likeValue) {
        synchronized (contentLock) {
            for (StoryListItemDTO listItemDTO : storyListItems.values()) {
                if (storyId.equals(Integer.toString(listItemDTO.id())) && listItemDTO.like() != likeValue) {
                    listItemDTO.like(likeValue);
                    break;
                }
            }
            for (StoryDTO storyDTO: stories.values()) {
                if (storyId.equals(Integer.toString(storyDTO.id())) && storyDTO.like() != likeValue) {
                    storyDTO.like(likeValue);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean addStoryToFavorite(@NonNull String storyId) {
        synchronized (contentLock) {
            StoryFeedDTO favFeed = feeds.get(favFeedKey);
            if (favFeed == null || favFeed.storiesIds.contains(storyId)) return false;
            favFeed.storiesIds.add(storyId);
        }
        return true;
    }

    @Override
    public boolean removeStoryFromFavorite(@NonNull String storyId) {
        synchronized (contentLock) {
            StoryFeedDTO favFeed = feeds.get(favFeedKey);
            if (favFeed == null) return false;
            return favFeed.storiesIds.remove(storyId);
        }
    }

    @Override
    public void removeAllFavorites() {
        synchronized (contentLock) {
            StoryFeedDTO favFeed = feeds.get(favFeedKey);
            if (favFeed == null) return;
            favFeed.storiesIds.clear();
            favoriteCovers.clear();
        }
    }

    @Override
    public Result<StoryDTO> getStoryById(@NonNull String storyId) {
        return null;
    }
}
