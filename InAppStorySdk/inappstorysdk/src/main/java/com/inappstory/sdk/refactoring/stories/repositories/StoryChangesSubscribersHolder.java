package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.stories.IStoryCoverCellChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.IStoryListItemChangeSubscriber;

import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoryChangesSubscribersHolder implements IStoryChangesSubscribersHolder {
    Map<String, Set<IStoryListItemChangeSubscriber>> listSubscribers = new HashMap<>();
    Set<IStoryCoverCellChangeSubscriber> coverCellSubscribers = new HashSet<>();
    private final Object subLock = new Object();

    @Override
    public void addStoryChangeSubscriber(IStoryListItemChangeSubscriber subscriber) {
        String storyId = subscriber.getStoryId();
        synchronized (subLock) {
            Set<IStoryListItemChangeSubscriber> listItemChangeSubscribers = listSubscribers.get(storyId);
            if (listItemChangeSubscribers == null) {
                listItemChangeSubscribers = new HashSet<>();
                listSubscribers.put(storyId, listItemChangeSubscribers);
            }
            listItemChangeSubscribers.add(subscriber);
        }
    }

    @Override
    public void removeStoryChangeSubscriber(IStoryListItemChangeSubscriber subscriber) {
        String storyId = subscriber.getStoryId();
        synchronized (subLock) {
            Set<IStoryListItemChangeSubscriber> listItemChangeSubscribers = listSubscribers.get(storyId);
            if (listItemChangeSubscribers == null) return;
            listItemChangeSubscribers.remove(subscriber);
        }
    }

    @Override
    public void addCoverCellChangeSubscriber(IStoryCoverCellChangeSubscriber subscriber) {
        synchronized (subLock) {
            coverCellSubscribers.add(subscriber);
        }
    }

    @Override
    public void removeCoverCellChangeSubscriber(IStoryCoverCellChangeSubscriber subscriber) {
        synchronized (subLock) {
            coverCellSubscribers.remove(subscriber);
        }
    }

    @Override
    public void notifyStoryListItemChange(StoryListItemDTO story) {
        String storyId = Integer.toString(story.id());
        List<IStoryListItemChangeSubscriber> localSubscribers = new ArrayList<>();
        synchronized (subLock) {
            Set<IStoryListItemChangeSubscriber> listItemChangeSubscribers = listSubscribers.get(storyId);
            if (listItemChangeSubscribers == null) return;
            localSubscribers.addAll(listItemChangeSubscribers);
        }
        for (IStoryListItemChangeSubscriber subscriber: localSubscribers) {
            subscriber.onChange(story);
        }
    }

    @Override
    public void notifyFavoriteCellChanges(List<StoryCoverDTO> covers) {
        List<IStoryCoverCellChangeSubscriber> localSubscribers = new ArrayList<>();
        synchronized (subLock) {
            localSubscribers.addAll(coverCellSubscribers);
        }
        List<StoryCoverDTO> coverDTOs = new ArrayList<>(covers);
        for (IStoryCoverCellChangeSubscriber subscriber: localSubscribers) {
            subscriber.onChange(coverDTOs);
        }
    }

    @Override
    public void notifyFavoriteFeedChanges(String storyId, boolean add) {

    }

    @Override
    public void destroy() {
        synchronized (subLock) {
            listSubscribers.clear();
        }
    }
}
