package com.inappstory.sdk.refactoring.stories.repositories;

import com.inappstory.sdk.refactoring.stories.IStoryChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;

public class StoryChangesSubscribersHolder implements IStoryChangesSubscribersHolder{
    @Override
    public void addStoryChangeSubscriber(IStoryChangeSubscriber subscriber) {

    }

    @Override
    public void removeStoryChangeSubscriber(IStoryChangeSubscriber subscriber) {

    }

    @Override
    public void notifyStoryChange(StoryDTO story) {

    }

    @Override
    public void notifyStoryListItemChange(StoryListItemDTO story) {

    }

    @Override
    public void notifyFavoriteCellChanges() {

    }

    @Override
    public void notifyFavoriteFeedChanges() {

    }
}
