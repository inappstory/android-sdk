package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.data.contracts.IStoryItem;
import com.inappstory.sdk.refactoring.stories.repositories.IStoryRepository;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoryListItemCoordinates;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderImmutableState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderOpenState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.views.SwipeDirection;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;

import java.util.ArrayList;
import java.util.List;

public class StoryReaderViewModel {
    private final IASCore core;

    public IASCore core() {
        return core;
    }

    public void pagerPageSelected(int newIndex) {
        List<String> ids = readerImmutableState.storiesIds();
        ContentType contentType = readerImmutableState.contentType();
        IStoryRepository storyRepository = core
                .storyRepository();

        IStoryItem storyListItem = storyRepository.getLocalStoryListItem(ids.get(newIndex));
        if (storyListItem == null) return;
        ContentIdAndType nextId = null;
        int nextCount = 0;
        int nextIndex = 0;
        ContentIdAndType prevId = null;
        int prevCount = 0;
        int prevIndex = 0;
        if (newIndex > 0) {

            IStoryItem storyListItemPrev = storyRepository.getLocalStoryListItem(ids.get(newIndex - 1));
            if (storyListItemPrev != null) {
                prevId = new ContentIdAndType(
                        storyListItemPrev.id(),
                        contentType
                );
                prevIndex = pageSlideIndexes.get(newIndex - 1);
                prevCount = storyListItemPrev.slidesCount();
            }
        }
        if (newIndex < ids.size() - 1) {
            IStoryItem storyListItemNext = storyRepository.getLocalStoryListItem(ids.get(newIndex + 1));
            if (storyListItemNext != null) {
                nextId = new ContentIdAndType(
                        storyListItemNext.id(),
                        contentType
                );
                nextIndex = pageSlideIndexes.get(newIndex + 1);
                nextCount = storyListItemNext.slidesCount();
            }
        }
        storyRepository.getLocalStoryListItem(ids.get(newIndex));
        ContentIdAndType mainId = new ContentIdAndType(
                Integer.parseInt(ids.get(newIndex)),
                contentType
        );
        core.storySlidesDownloadManager().renewAllPriorities(
                mainId,
                pageSlideIndexes.get(newIndex),
                storyListItem.slidesCount(),
                prevId,
                prevIndex,
                prevCount,
                nextId,
                nextIndex,
                nextCount
        );
        core.storyDownloadManager().addStories(mainId, nextId, prevId);
    }

    public void pagerPageScrollStateChanged(int newState) {

    }

    public void pagerPageScrolled(int position, float positionOffset) {

    }

    public StoryReaderImmutableState readerImmutableState() {
        return readerImmutableState;
    }

    private StoryReaderImmutableState readerImmutableState = null;

    public LaunchStoryScreenAppearance appearanceSettings = null;

    private final Observable<StoryReaderState> storyReaderStateObservable =
            new Observable<>(new StoryReaderState());

    final List<Integer> pageSlideIndexes = new ArrayList<>();

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

    private void tryToCloseReader(int action) {
        updateOpenState(StoryReaderOpenState.CLOSING);
    }

    public void changePageOpenedIndex(int page, int slideIndex) {
        pageSlideIndexes.set(page, slideIndex);
    }

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
        pageSlideIndexes.clear();
        for (int i = 0; i < immutableState.storiesIds().size(); i++) {
            pageSlideIndexes.add(0);
        }
        if (state.openState() == StoryReaderOpenState.IDLE) {
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

    public void initStartState(int index, int slideIndex) {
        storyReaderStateObservable.updateValue(readerState().copy().currentPage(index));
    }

    public void initAppearanceSettings(LaunchStoryScreenAppearance appearanceSettings) {
        this.appearanceSettings = appearanceSettings;
    }

    public void initImmutableState(StoryReaderImmutableState state) {
        this.readerImmutableState = state;
    }

    public StoryReaderViewModel(IASCore core) {
        this.core = core;
    }
}
