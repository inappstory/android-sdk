package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoryListItemCoordinates;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderImmutableState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderOpenState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderState;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;

import java.util.List;

public class StoryReaderViewModel {
    private final IASCore core;

    public StoryReaderImmutableState readerImmutableState() {
        return readerImmutableState;
    }

    private StoryReaderImmutableState readerImmutableState = null;

    private final Observable<StoryReaderState> storyReaderStateObservable =
            new Observable<>(new StoryReaderState());

    public StoryReaderState getReaderState() {
        return storyReaderStateObservable.getValue();
    }

    public void addSubscriber(Observer<StoryReaderState> observer) {
        storyReaderStateObservable.subscribeAndGetValue(observer);
    }

    public void removeSubscriber(Observer<StoryReaderState> observer) {
        storyReaderStateObservable.unsubscribe(observer);
    }

    public void cleanReaderData() {
        storyReaderStateObservable.updateValue(new StoryReaderState());
        this.readerImmutableState = null;
    }

    public boolean openReader(
            StoryReaderImmutableState immutableState,
            StoryListItemCoordinates startedCoordinates
    ) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        this.readerImmutableState = immutableState;
        if (state.openState() == StoryReaderOpenState.CLOSED) {
            storyReaderStateObservable.updateValue(
                    new StoryReaderState()
                            .currentCoordinates(startedCoordinates)
            );
            return true;
        } else {
            return false;
        }
    }

    public void currentCoordinates(StoryListItemCoordinates currentCoordinates) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().currentCoordinates(currentCoordinates)
        );
    }

    public void horizontalSwipeIsAllowed(boolean horizontalSwipeIsAllowed) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().horizontalSwipeIsAllowed(horizontalSwipeIsAllowed)
        );
    }

    public void verticalSwipeIsAllowed(boolean verticalSwipeIsAllowed) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().verticalSwipeIsAllowed(verticalSwipeIsAllowed)
        );
    }

    public void swipeUpIsAllowed(boolean swipeUpIsAllowed) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().swipeUpAllowed(swipeUpIsAllowed)
        );
    }

    public void closeIsAllowed(boolean closeIsAllowed) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().closeAllowed(closeIsAllowed)
        );
    }

    public void backPressEnabled(boolean backPressEnabled) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().backPressEnabled(backPressEnabled)
        );
    }

    public void horizontalSwipeInProgress(boolean horizontalSwipeInProgress) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().horizontalSwipeInProgress(horizontalSwipeInProgress)
        );

    }

    public void verticalSwipeInProgress(boolean verticalSwipeInProgress) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().verticalSwipeInProgress(verticalSwipeInProgress)
        );
    }

    public void closeReader(String reason) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        if (state.openState() == StoryReaderOpenState.OPENED) {
            storyReaderStateObservable.updateValue(
                    state.copy().openState(StoryReaderOpenState.CLOSING)
            );
        }
    }

    public void forceCloseReader() {
        StoryReaderState state = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(
                state.copy().openState(StoryReaderOpenState.FORCE_CLOSING)
        );
    }

    public void navigateToIndex(int index, int action) {

    }

    public StoryReaderViewModel(IASCore core) {
        this.core = core;
    }
}
