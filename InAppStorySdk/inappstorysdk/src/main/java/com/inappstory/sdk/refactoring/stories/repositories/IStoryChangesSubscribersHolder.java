package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.stories.IStoryCoverCellChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.IStoryListItemChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;

import java.util.List;

public interface IStoryChangesSubscribersHolder {
    void addStoryChangeSubscriber(IStoryListItemChangeSubscriber subscriber);
    void removeStoryChangeSubscriber(IStoryListItemChangeSubscriber subscriber);
    void addCoverCellChangeSubscriber(IStoryCoverCellChangeSubscriber subscriber);
    void removeCoverCellChangeSubscriber(IStoryCoverCellChangeSubscriber subscriber);
    void notifyStoryListItemChange(StoryListItemDTO story);
    void notifyFavoriteCellChanges(List<StoryCoverDTO> covers);
    void notifyFavoriteFeedChanges(String storyId, boolean add);
    void destroy();
}
