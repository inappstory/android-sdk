package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageState;

public class StoryReaderPageViewModel {

    private final Observable<StoryReaderPageLoaderState> storyReaderPageLoaderStateObservable =
            new Observable<>(new StoryReaderPageLoaderState());

    private final Observable<StoryReaderPageState> storyReaderPageStateObservable =
            new Observable<>(new StoryReaderPageState());

    public void addLoaderStateSubscriber(Observer<StoryReaderPageLoaderState> observer) {
        storyReaderPageLoaderStateObservable.subscribeAndGetValue(observer);
    }

    public void addPageStateSubscriber(Observer<StoryReaderPageState> observer) {
        storyReaderPageStateObservable.subscribeAndGetValue(observer);
    }

    public void removeLoaderStateSubscriber(Observer<StoryReaderPageLoaderState> observer) {
        storyReaderPageLoaderStateObservable.unsubscribe(observer);
    }

    public void clickOnRefresh() {}
}
