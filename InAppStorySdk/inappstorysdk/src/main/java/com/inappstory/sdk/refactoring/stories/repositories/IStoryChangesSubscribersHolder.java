package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.stories.IStoryChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;

public interface IStoryChangesSubscribersHolder {
    void addStoryChangeSubscriber(IStoryChangeSubscriber subscriber);
    void removeStoryChangeSubscriber(IStoryChangeSubscriber subscriber);
    void notifyStoryChange(StoryDTO story);
    void notifyStoryListItemChange(StoryListItemDTO story);
    void notifyFavoriteCellChanges();
    void notifyFavoriteFeedChanges();
}
