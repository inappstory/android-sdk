package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.stories.IStoriesFavoriteFeedChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.IStoriesListFavoriteCellChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.IStoriesListItemChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;

import java.util.List;

public interface IStoryChangesSubscribersHolder {
    void addStoryChangeSubscriber(IStoriesListItemChangeSubscriber subscriber);
    void removeStoryChangeSubscriber(IStoriesListItemChangeSubscriber subscriber);
    void addCoverCellChangeSubscriber(IStoriesListFavoriteCellChangeSubscriber subscriber);
    void removeCoverCellChangeSubscriber(IStoriesListFavoriteCellChangeSubscriber subscriber);
    void addFavoriteFeedChangeSubscriber(IStoriesFavoriteFeedChangeSubscriber subscriber);
    void removeFavoriteFeedChangeSubscriber(IStoriesFavoriteFeedChangeSubscriber subscriber);
    void notifyStoryListItemChange(StoriesListItemDTO story);
    void notifyFavoriteCellChanges(List<StoryCoverDTO> covers);
    void notifyFavoriteFeedChanges(String storyId, boolean add);
    void destroy();
}
