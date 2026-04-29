package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoryListItemCoordinates;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderImmutableState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderOpenState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.views.SwipeDirection;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;

public class StoryReaderViewModel {
    private final IASCore core;

    public IASCore core() {
        return core;
    }

    public StoryReaderImmutableState readerImmutableState() {
        return readerImmutableState;
    }

    private StoryReaderImmutableState readerImmutableState = null;

    private final Observable<StoryReaderState> storyReaderStateObservable =
            new Observable<>(new StoryReaderState());

    public StoryReaderState readerState() {
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

    int latestShowStoryAction = ShowStory.ACTION_OPEN;

    public void openNextPage(int action) {
        StoryReaderState currentState = storyReaderStateObservable.getValue();
        int page = currentState.currentPage();
        if (page >= readerImmutableState.storiesIds().size() - 1) {
            //TODO close story reader
            throw new NotImplementedMethodException();
        } else {
            latestShowStoryAction = action;
            storyReaderStateObservable.updateValue(currentState.currentPage(page + 1));
        }
    }

    public void closeReader(boolean forceClose, int action) {
        updateOpenState(
                forceClose ?
                StoryReaderOpenState.FORCE_CLOSING :
                StoryReaderOpenState.CLOSING
        );
    }

    public void handleBackPress() {
        StoryReaderState currentState = storyReaderStateObservable.getValue();
        if (currentState.shareDataState() != null) {

        } else if (currentState.goodsV1WidgetState() != null) {

        } else if (currentState.reviewDialogState() != null) {

        } else {
            tryToCloseReader(CloseStory.CLICK);
        }
    }

    private void tryToCloseReader(int action) {}

    public void updateOpenState(StoryReaderOpenState openState) {
        StoryReaderState currentState = storyReaderStateObservable.getValue();
        storyReaderStateObservable.updateValue(currentState.copy().openState(openState));
    }

    public void openPreviousPage(int action) {
        StoryReaderState currentState = storyReaderStateObservable.getValue();
        int page = currentState.currentPage();
        if (page > 0) {
            latestShowStoryAction = action;
            storyReaderStateObservable.updateValue(currentState.currentPage(page - 1));
        }
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
                            .openState(StoryReaderOpenState.OPENING)
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

    public void swipe(int pageIndex, SwipeDirection direction) {
    }

    public StoryReaderViewModel(IASCore core) {
        this.core = core;
    }
}
