package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderButtonsState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageState;

public class StoryReaderPageViewModel {
    private final IASCore core;
    private final StoryReaderViewModel readerViewModel;

    private final Observable<StoryReaderPageLoaderState> storyReaderPageLoaderStateObservable =
            new Observable<>(new StoryReaderPageLoaderState());

    private final Observable<StoryReaderPageState> storyReaderPageStateObservable =
            new Observable<>(new StoryReaderPageState());

    private final Observable<StoryReaderButtonsState> storyReaderPageButtonsStateObservable =
            new Observable<>(new StoryReaderButtonsState());

    public StoryReaderPageViewModel(IASCore core, StoryReaderViewModel readerViewModel) {
        this.core = core;
        this.readerViewModel = readerViewModel;
    }

    public void addLoaderStateSubscriber(Observer<StoryReaderPageLoaderState> observer) {
        storyReaderPageLoaderStateObservable.subscribeAndGetValue(observer);
    }

    public void removeLoaderStateSubscriber(Observer<StoryReaderPageLoaderState> observer) {
        storyReaderPageLoaderStateObservable.unsubscribe(observer);
    }

    public void addPageStateSubscriber(Observer<StoryReaderPageState> observer) {
        storyReaderPageStateObservable.subscribeAndGetValue(observer);
    }

    public void removePageStateSubscriber(Observer<StoryReaderPageState> observer) {
        storyReaderPageStateObservable.unsubscribe(observer);
    }

    public void addButtonsStateSubscriber(Observer<StoryReaderButtonsState> observer) {
        storyReaderPageButtonsStateObservable.subscribeAndGetValue(observer);
    }

    public void removeButtonsStateSubscriber(Observer<StoryReaderButtonsState> observer) {
        storyReaderPageButtonsStateObservable.unsubscribe(observer);
    }


    public void clickOnRefresh() {}

    public void likeClick() {}
    public void dislikeClick() {}
    public void favoriteClick() {}
    public void soundClick() {}
    public void shareClick() {}
}
