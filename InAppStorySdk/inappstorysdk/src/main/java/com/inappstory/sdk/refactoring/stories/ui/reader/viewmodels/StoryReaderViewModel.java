package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoryListItemCoordinates;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderOpenState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderState;

import java.util.List;

public class StoryReaderViewModel {
    private final IASCore core;

    private final Observable<StoryReaderState> storyReaderStateObservable =
            new Observable<>(new StoryReaderState());


    public void cleanReaderData() {
        storyReaderStateObservable.updateValue(new StoryReaderState());
    }

    public boolean openReader(
            List<String> storiesIds,
            StoryListItemCoordinates startedCoordinates
    ) {
        StoryReaderState state = storyReaderStateObservable.getValue();
        if (state.openState() == StoryReaderOpenState.CLOSED) {
            storyReaderStateObservable.updateValue(
                    new StoryReaderState()
                            .storiesIds(storiesIds)
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

    public void closeReader() {
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
                state.copy().openState(StoryReaderOpenState.CLOSING)
        );
    }

    public StoryReaderViewModel(IASCore core) {
        this.core = core;
    }
}
