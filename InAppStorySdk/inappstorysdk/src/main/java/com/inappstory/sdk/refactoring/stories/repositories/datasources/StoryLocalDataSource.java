package com.inappstory.sdk.refactoring.stories.repositories.datasources;

import androidx.annotation.NonNull;

import com.inappstory.sdk.refactoring.core.utils.results.Error;
import com.inappstory.sdk.refactoring.core.utils.results.Result;
import com.inappstory.sdk.refactoring.core.utils.results.Success;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.usecases.StoriesFeedParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryLocalDataSource implements IStoryLocalDataSource {

    private final List<StoryCoverDTO> favoriteCovers = new ArrayList<>();
    private boolean favoriteCoversLoaded = false;
    private final Map<String, StoriesListItemDTO> storyListItems = new HashMap<>();
    private final Map<String, StoryDTO> stories = new HashMap<>();
    private final Map<StoriesFeedParameters, StoryFeedDTO> feeds = new HashMap<>();
    private final Object contentLock = new Object();

    public StoryLocalDataSource() {

    }

    @Override
    public Result<StoryFeedDTO> getStoriesFeed(@NonNull StoriesFeedParameters feedParameters) {
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
    public void setStoryCovers(@NonNull List<StoryCoverDTO> storyCovers) {
        synchronized (contentLock) {
            favoriteCovers.clear();
            favoriteCovers.addAll(storyCovers);
        }
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
    public List<StoryCoverDTO> getFavoriteCovers() {
        synchronized (contentLock) {
            return favoriteCovers;
        }
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
    public boolean addOrUpdateStoryListItem(@NonNull StoriesListItemDTO story) {
        String key = Integer.toString(story.id());
        synchronized (contentLock) {
            if (Objects.equals(this.storyListItems.get(key), story)) return false;
            this.storyListItems.put(key, story);
        }
        return true;
    }

    @Override
    public boolean addOrUpdateStoriesFeed(@NonNull StoriesFeedParameters feedParameters, @NonNull StoryFeedDTO feed) {
        synchronized (contentLock) {
            if (Objects.equals(this.feeds.get(feedParameters), feed)) return false;
            this.feeds.put(feedParameters, feed);
        }
        return true;
    }

    @Override
    public boolean likeDislikeStory(@NonNull String storyId, int likeValue) {
        synchronized (contentLock) {
            for (StoriesListItemDTO listItemDTO : storyListItems.values()) {
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
    public void removeAllFavorites() {
        synchronized (contentLock) {
            favoriteCovers.clear();
        }
    }

    @Override
    public Result<StoryDTO> getStoryById(@NonNull String storyId) {
        synchronized (contentLock) {
            StoryDTO storyDTO = this.stories.get(storyId);
            if (storyDTO == null) return new Error<>("No local item with id: " + storyId);
            else return new Success<>(storyDTO);
        }
    }

    @Override
    public Result<StoriesListItemDTO> getStoryListItemById(@NonNull String storyId) {
        synchronized (contentLock) {
            StoriesListItemDTO storiesListItemDTO = this.storyListItems.get(storyId);
            if (storiesListItemDTO == null) return new Error<>("No local item with id: " + storyId);
            else return new Success<>(storiesListItemDTO);
        }
    }

    @Override
    public void destroy() {

    }
}
