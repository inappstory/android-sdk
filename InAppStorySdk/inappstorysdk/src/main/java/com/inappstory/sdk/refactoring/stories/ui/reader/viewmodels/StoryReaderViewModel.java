package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;



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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryReaderViewModel {
    private final IASCore core;

    public IASCore core() {
        return core;
    }

    private final Map<String, StoryReaderPageViewModel> pageViewModels = new HashMap<>();

    public StoryReaderPageViewModel getOrCreatePageViewModel(String storyId) {
        StoryReaderPageViewModel pageViewModel = pageViewModels.get(storyId);
        if (pageViewModel == null) {
            pageViewModel = new StoryReaderPageViewModel(
                    this,
                    storyId,
                    readerImmutableState().storiesIds().indexOf(storyId)
            );
            pageViewModels.put(storyId, pageViewModel);
        }
        return pageViewModel;
    }

    public void destroyPage(int position) {
        String key = readerImmutableState().storiesIds().get(position);
        StoryReaderPageViewModel pageViewModel =
                pageViewModels.get(key);
        if (pageViewModel != null) pageViewModel.destroy();
        pageViewModels.remove(key);
    }

    public void destroyPages() {
        for (StoryReaderPageViewModel pageViewModel :
                pageViewModels.values()) {
            if (pageViewModel != null) pageViewModel.destroy();
        }
        pageViewModels.clear();
    }


    private void loadSlidesForPage(int newIndex) {
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

    public void pagerPageSelected(int newIndex) {
        if (newIndex == readerState().currentPage()) return;
        storyReaderStateObservable.setValue(
                readerState()
                .copy()
                .currentPage(newIndex)
        );
        String currentId = readerImmutableState().storiesIds().get(newIndex);
        for (Map.Entry<String, StoryReaderPageViewModel> pageViewModelEntries : pageViewModels.entrySet()) {
            if (Objects.equals(pageViewModelEntries.getKey(), currentId)) {
                pageViewModelEntries.getValue().startSlide();
            } else {
                pageViewModelEntries.getValue().stopSlide();
            }
        }
        loadSlidesForPage(newIndex);
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

    public final List<Integer> pageSlideIndexes = new ArrayList<>();

    public StoryReaderState readerState() {
        return storyReaderStateObservable.getValue();
    }

    public StoryReaderState readerStateCopy() {
        return storyReaderStateObservable.getValue().copy();
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
        StoryReaderState currentState = readerStateCopy();
        int page = currentState.currentPage();
        if (page >= readerImmutableState.storiesIds().size() - 1) {
            //TODO close story reader
            throw new NotImplementedMethodException();
        } else {
            latestShowStoryAction = action;
            storyReaderStateObservable.updateValue(currentState.currentPredictedPage(page + 1));
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
        StoryReaderState currentState = readerStateCopy();
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
        StoryReaderState currentState = readerStateCopy();
        storyReaderStateObservable.updateValue(currentState.openState(openState));
    }


    public void openPreviousPage(int action) {
        StoryReaderState currentState = readerStateCopy();
        int page = currentState.currentPage();
        if (page > 0) {
            latestShowStoryAction = action;
            storyReaderStateObservable.updateValue(currentState.currentPredictedPage(page - 1));
        }
    }

    public boolean openReader(
            StoryReaderImmutableState immutableState,
            StoryListItemCoordinates startedCoordinates
    ) {
        StoryReaderState state = readerState();
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
        storyReaderStateObservable.updateValue(
                readerStateCopy().currentCoordinates(currentCoordinates)
        );
    }

    public void horizontalSwipeIsAllowed(boolean horizontalSwipeIsAllowed) {
        storyReaderStateObservable.updateValue(
                readerStateCopy().horizontalSwipeIsAllowed(horizontalSwipeIsAllowed)
        );
    }

    public void verticalSwipeIsAllowed(boolean verticalSwipeIsAllowed) {
        storyReaderStateObservable.updateValue(
                readerStateCopy().verticalSwipeIsAllowed(verticalSwipeIsAllowed)
        );
    }

    public void swipeUpIsAllowed(boolean swipeUpIsAllowed) {
        storyReaderStateObservable.updateValue(
                readerStateCopy().swipeUpAllowed(swipeUpIsAllowed)
        );
    }

    public void closeIsAllowed(boolean closeIsAllowed) {
        storyReaderStateObservable.updateValue(
                readerStateCopy().closeAllowed(closeIsAllowed)
        );
    }

    public void backPressEnabled(boolean backPressEnabled) {
        storyReaderStateObservable.updateValue(
                readerStateCopy().backPressEnabled(backPressEnabled)
        );
    }

    public void horizontalSwipeInProgress(boolean horizontalSwipeInProgress) {
        storyReaderStateObservable.updateValue(
                readerStateCopy().horizontalSwipeInProgress(horizontalSwipeInProgress)
        );

    }

    public void verticalSwipeInProgress(boolean verticalSwipeInProgress) {
        storyReaderStateObservable.updateValue(
                readerStateCopy().verticalSwipeInProgress(verticalSwipeInProgress)
        );
    }

    public void closeReader(String reason) {
        StoryReaderState state = readerStateCopy();
        if (state.openState() == StoryReaderOpenState.OPENED) {
            storyReaderStateObservable.updateValue(
                    state.openState(StoryReaderOpenState.CLOSING)
            );
        }
    }

    public void forceCloseReader() {
        storyReaderStateObservable.updateValue(
                readerStateCopy().openState(StoryReaderOpenState.FORCE_CLOSING)
        );
    }

    public void navigateToIndex(int index, int action) {
        latestShowStoryAction = action;
        storyReaderStateObservable.updateValue(readerStateCopy().currentPredictedPage(index));
    }

    public void swipe(int pageIndex, SwipeDirection direction) {
    }

    public void initStartState(int index, int slideIndex) {
        storyReaderStateObservable.updateValue(readerStateCopy()
                .currentPredictedPage(index)
                .currentPage(index)
        );
        loadSlidesForPage(index);
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
