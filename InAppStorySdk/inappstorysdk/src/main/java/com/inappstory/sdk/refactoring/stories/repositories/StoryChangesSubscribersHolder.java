package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.stories.IStoriesFavoriteFeedChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.IStoriesListFavoriteCellChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.IStoriesListItemChangeSubscriber;

import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoryChangesSubscribersHolder implements IStoryChangesSubscribersHolder {
    Map<String, Set<IStoriesListItemChangeSubscriber>> listSubscribers = new HashMap<>();
    Set<IStoriesListFavoriteCellChangeSubscriber> coverCellSubscribers = new HashSet<>();
    Set<IStoriesFavoriteFeedChangeSubscriber> favoriteFeedChangeSubscribers = new HashSet<>();
    private final Object subLock = new Object();

    @Override
    public void addStoryChangeSubscriber(IStoriesListItemChangeSubscriber subscriber) {
        String storyId = subscriber.getStoryId();
        synchronized (subLock) {
            Set<IStoriesListItemChangeSubscriber> listItemChangeSubscribers = listSubscribers.get(storyId);
            if (listItemChangeSubscribers == null) {
                listItemChangeSubscribers = new HashSet<>();
                listSubscribers.put(storyId, listItemChangeSubscribers);
            }
            listItemChangeSubscribers.add(subscriber);
        }
    }

    @Override
    public void removeStoryChangeSubscriber(IStoriesListItemChangeSubscriber subscriber) {
        String storyId = subscriber.getStoryId();
        synchronized (subLock) {
            Set<IStoriesListItemChangeSubscriber> listItemChangeSubscribers = listSubscribers.get(storyId);
            if (listItemChangeSubscribers == null) return;
            listItemChangeSubscribers.remove(subscriber);
        }
    }

    @Override
    public void addCoverCellChangeSubscriber(IStoriesListFavoriteCellChangeSubscriber subscriber) {
        synchronized (subLock) {
            coverCellSubscribers.add(subscriber);
        }
    }

    @Override
    public void removeCoverCellChangeSubscriber(IStoriesListFavoriteCellChangeSubscriber subscriber) {
        synchronized (subLock) {
            coverCellSubscribers.remove(subscriber);
        }
    }

    @Override
    public void addFavoriteFeedChangeSubscriber(IStoriesFavoriteFeedChangeSubscriber subscriber) {
        synchronized (subLock) {
            favoriteFeedChangeSubscribers.add(subscriber);
        }
    }

    @Override
    public void removeFavoriteFeedChangeSubscriber(IStoriesFavoriteFeedChangeSubscriber subscriber) {
        synchronized (subLock) {
            favoriteFeedChangeSubscribers.remove(subscriber);
        }
    }

    @Override
    public void notifyStoryListItemChange(StoriesListItemDTO story) {
        String storyId = Integer.toString(story.id());
        List<IStoriesListItemChangeSubscriber> localSubscribers = new ArrayList<>();
        synchronized (subLock) {
            Set<IStoriesListItemChangeSubscriber> listItemChangeSubscribers = listSubscribers.get(storyId);
            if (listItemChangeSubscribers == null) return;
            localSubscribers.addAll(listItemChangeSubscribers);
        }
        for (IStoriesListItemChangeSubscriber subscriber : localSubscribers) {
            subscriber.onChange(story);
        }
    }

    @Override
    public void notifyFavoriteCellChanges(List<StoryCoverDTO> covers) {
        List<IStoriesListFavoriteCellChangeSubscriber> localSubscribers = new ArrayList<>();
        synchronized (subLock) {
            localSubscribers.addAll(coverCellSubscribers);
        }
        List<StoryCoverDTO> coverDTOs = new ArrayList<>(covers);
        for (IStoriesListFavoriteCellChangeSubscriber subscriber : localSubscribers) {
            subscriber.onChange(coverDTOs);
        }
    }

    @Override
    public void notifyFavoriteFeedChanges(String storyId, boolean add) {
        List<IStoriesFavoriteFeedChangeSubscriber> localSubscribers = new ArrayList<>();
        synchronized (subLock) {
            localSubscribers.addAll(favoriteFeedChangeSubscribers);
        }
        for (IStoriesFavoriteFeedChangeSubscriber subscriber : localSubscribers) {
            subscriber.onChange(storyId, add);
        }
    }

    @Override
    public void destroy() {
        synchronized (subLock) {
            listSubscribers.clear();
            favoriteFeedChangeSubscribers.clear();
            coverCellSubscribers.clear();
        }
    }
}
