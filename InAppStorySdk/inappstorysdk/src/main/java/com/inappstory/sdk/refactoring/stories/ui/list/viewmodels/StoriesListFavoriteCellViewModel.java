package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.IStoriesListFavoriteCellChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListFavoriteCellState;

import java.util.List;

public class StoriesListFavoriteCellViewModel implements IStoriesListFavoriteCellChangeSubscriber {
    private final Observable<StoriesListFavoriteCellState> storiesListItemStateObservable =
            new Observable<>(null);


    private final IASCore core;

    public StoriesListFavoriteCellViewModel(
            IASCore core
    ) {
        this.core = core;
        core.storyChangesSubscribers().addCoverCellChangeSubscriber(this);
    }

    public void addSubscriber(Observer<StoriesListFavoriteCellState> observer) {
        storiesListItemStateObservable.subscribeAndGetValue(observer);
    }

    public void removeSubscriber(Observer<StoriesListFavoriteCellState> observer) {
        storiesListItemStateObservable.unsubscribe(observer);
    }

    @Override
    public void onChange(List<StoryCoverDTO> covers) {
        storiesListItemStateObservable.updateValue(new StoriesListFavoriteCellState(
                covers
        ));
    }
}
