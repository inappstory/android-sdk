package com.inappstory.sdk.stories.uidomain.reader.page;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.core.models.js.StoryIdSlideIndex;
import com.inappstory.sdk.core.models.js.StoryLinkObject;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.IStoryTimelineManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimelineManager;
import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.BottomPanelViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.IStoriesDisplayViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.SlideLoadState;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.StoriesWebViewDisplayViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.StoryDisplayState;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallbackWithContext;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackCallToAction;
import com.inappstory.sdk.utils.ArrayUtil;
import com.inappstory.sdk.utils.SingleTimeLiveEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class StoriesReaderPageViewModel implements IStoriesReaderPageViewModel {
    public StoriesReaderPageViewModel(StoriesReaderPageState initState) {
        this.state = initState;
        bottomPanelViewModel = new BottomPanelViewModel(this);
        storiesDisplayViewModel = new StoriesWebViewDisplayViewModel(this);
        timelineManager = new StoryTimelineManager();
        timerManager = new TimerManager();
        IStoryDTO storyDTO = IASCore.getInstance().getStoriesRepository(
                initState.getStoryType()).getStoryById(
                initState.storyId()
        );
        setStoryModel(storyDTO);
        IASCore.getInstance().downloadManager.addSubscriber(this);
    }

    private void setStoryModel(IStoryDTO storyDTO) {
        if (storyDTO == null) return;
        storyModel.postValue(storyDTO);
        timelineManager.setSlidesCount(storyDTO.getSlidesCount());
        timelineManager.setDurations(ArrayUtil.toIntegerList(storyDTO.getDurations()), true);
    }

    private final StoriesReaderPageState state;

    private MutableLiveData<IStoryDTO> storyModel = new MutableLiveData<>();

    public StoriesReaderPageState getState() {
        return state;
    }

    private final BottomPanelViewModel bottomPanelViewModel;
    private final IStoriesDisplayViewModel storiesDisplayViewModel;
    private final IStoryTimelineManager timelineManager;
    private final TimerManager timerManager;

    public BottomPanelViewModel getBottomPanelViewModel() {
        return bottomPanelViewModel;
    }

    @Override
    public IStoryTimelineManager getTimelineManager() {
        return timelineManager;
    }

    @Override
    public void cacheStoryLoaded() {
        IStoryDTO storyDTO = IASCore.getInstance().getStoriesRepository(
                state.getStoryType()).getStoryById(
                state.storyId()
        );
        setStoryModel(storyDTO);
    }

    private final String uuid = UUID.randomUUID().toString();

    @Override
    public String viewModelUUID() {
        return uuid;
    }

    @NonNull
    @Override
    public String toString() {
        return "pageViewModel " + uuid;
    }

    private final Set<Integer> loadedSlides = new HashSet<>();
    private final Object loadedSlidesLock = new Object();


    @Override
    public void cacheSlideLoaded(int slideIndex) {
        Log.e("cacheSlideLoaded", state.storyId() + " " + slideIndex + " " + currentSlide.getValue());
        synchronized (loadedSlidesLock) {
            loadedSlides.add(slideIndex);
        }
        if (Objects.equals(currentSlide.getValue(), slideIndex)) {
            storiesDisplayViewModel.setStoryDisplayState(new StoryDisplayState(slideIndex));
        }
    }

    @Override
    public void jsSlideLoaded(int slideIndex) {
        if (Objects.equals(currentSlide.getValue(), slideIndex)) {
            storiesDisplayViewModel.jsCallStartSlide();
        } else {
            storiesDisplayViewModel.jsCallStopSlide();
        }
    }

    private int getSlideIndex() {
        Integer slideIndex = currentSlide.getValue();
        if (slideIndex == null) slideIndex = 0;
        return slideIndex;
    }

    @Override
    public void jsSlideStarted() {
        if (storyModel.getValue() == null) return;
        int slideIndex = getSlideIndex();
        timelineManager.startSegment(slideIndex);
        timelineManager.active(true);
        timerManager.setCurrentDuration(storyModel.getValue().getDurations()[slideIndex]);
        timerManager.startCurrentTimer();
        storiesDisplayViewModel.jsCallResumeSlide();
    }

    @Override
    public IStoriesDisplayViewModel displayViewModel() {
        return storiesDisplayViewModel;
    }

    private final MutableLiveData<Integer> currentSlide = new MutableLiveData<>(0);

    public void updateCurrentSlide(int currentSlide) {
        this.currentSlide.postValue(currentSlide);
        boolean hasSlide;
        synchronized (loadedSlidesLock) {
            hasSlide = loadedSlides.contains(currentSlide);
        }
        Log.e("cacheSlideLoaded", state.storyId() + " " + this.currentSlide.getValue() + " " + currentSlide + " hasSlide: " + hasSlide);
        if (Objects.equals(this.currentSlide.getValue(), currentSlide)) return;
        if (hasSlide) {
            storiesDisplayViewModel.setStoryDisplayState(new StoryDisplayState(currentSlide));
        }
    }


    public LiveData<Integer> currentSlideLD() {
        return currentSlide;
    }

    @Override
    public LiveData<IStoryDTO> storyModel() {
        return storyModel;
    }

    private final MutableLiveData<Boolean> isActive = new MutableLiveData<>(false);

    public void updateIsActive(boolean isActive) {
        this.isActive.postValue(isActive);
    }

    public LiveData<Boolean> isActiveLD() {
        return isActive;
    }

    private final MutableLiveData<SlideLoadState> slideLoadState
            = new MutableLiveData<>(new SlideLoadState());

    private final SingleTimeLiveEvent<IUseCaseCallbackWithContext> eventWithContext
            = new SingleTimeLiveEvent<>();

    @Override
    public LiveData<SlideLoadState> slideLoadState() {
        return slideLoadState;
    }

    @Override
    public LiveData<IUseCaseCallbackWithContext> eventWithContext() {
        return eventWithContext;
    }

    @Override
    public void shareClick() {

    }

    @Override
    public void likeClick() {

    }

    @Override
    public void dislikeClick() {

    }

    @Override
    public void favoriteClick() {

    }

    @Override
    public void soundClick() {

    }

    @Override
    public void destroy() {
        IASCore.getInstance().downloadManager.removeSubscriber(this);
    }

    @Override
    public void openNextSlide() {
        IStoryDTO storyDTO = storyModel.getValue();
        if (storyDTO == null) return;
        timerManager.setTimerDuration(0);
        int lastIndex = getSlideIndex();
        if (lastIndex < storyDTO.getSlidesCount() - 1) {
            if (storiesDisplayViewModel != null)
                storiesDisplayViewModel.jsCallStopSlide();
            lastIndex++;
            IASCore.getInstance().getStoriesRepository(state.getStoryType())
                    .setStoryLastIndex(state.storyId(), lastIndex);
            updateCurrentSlide(lastIndex);
        } else {
            IASUICore.getInstance().getStoriesReaderVM().nextStory();
        }
    }

    @Override
    public void openPrevSlide() {
        IStoryDTO storyDTO = storyModel.getValue();
        if (storyDTO == null) return;
        timerManager.setTimerDuration(0);
        int lastIndex = getSlideIndex();
        if (lastIndex > 0) {
            if (storiesDisplayViewModel != null)
                storiesDisplayViewModel.jsCallStopSlide();
            lastIndex--;
            IASCore.getInstance().getStoriesRepository(state.getStoryType())
                    .setStoryLastIndex(state.storyId(), lastIndex);
            updateCurrentSlide(lastIndex);
        } else if (this.state.listIndex() == 0) {
            restartSlide();
        } else {
            IASUICore.getInstance().getStoriesReaderVM().prevStory();
        }
    }

    private void restartSlide() {
        IStoryDTO storyDTO = storyModel.getValue();
        if (storyDTO == null) return;
        List<Integer> durations = ArrayUtil.toIntegerList(storyDTO.getDurations());
        timelineManager.setDurations(durations, false);
        int slideIndex = getSlideIndex();
        timelineManager.startSegment(slideIndex);
        timerManager.restartTimer(durations.get(slideIndex));
    }

    @Override
    public void clickWithPayload(String payload) {
        IStoryDTO storyDTO = storyModel.getValue();
        if (storyDTO == null) return;
        StoryLinkObject object = JsonParser.fromJson(payload, StoryLinkObject.class);
        if (object == null) return;
        int lastIndex = IASCore.getInstance().getStoriesRepository(state.getStoryType())
                .getStoryLastIndex(state.storyId());
        ClickAction action = ClickAction.BUTTON;
        IStoriesReaderViewModel readerViewModel = IASUICore.getInstance().getStoriesReaderVM();
        switch (object.getLink().getType()) {
            case "url":
                if (object.getType() != null && !object.getType().isEmpty()) {
                    if ("swipeUpLink".equals(object.getType())) {
                        action = ClickAction.SWIPE;
                    }
                }
                if (state.getStoryType() == Story.StoryType.COMMON)
                    IASCore.getInstance().statisticV1Repository.setTypeToTransition(
                            new StoryIdSlideIndex(state.storyId(), lastIndex)
                    );
                eventWithContext.postValue(new UseCaseCallbackCallToAction(
                        object.getLink().getTarget(),
                        new SlideData(
                                new StoryData(
                                        storyDTO,
                                        readerViewModel.getState().launchData().getFeed(),
                                        readerViewModel.getState().launchData().getSourceType()
                                ),
                                lastIndex
                        ),
                        action
                ));
                break;
            case "json":
                if (object.getType() != null && !object.getType().isEmpty()) {
                    if ("swipeUpItems".equals(object.getType())) {
                        showGoods(
                                object.getLink().getTarget(),
                                object.getElementId(),
                                storyDTO.getId(),
                                lastIndex
                        );
                    }
                }
                break;
            default:
                break;
        }
    }

    private void showGoods(String skus, String widgetId, int storyId, int slideIndex) {
    }
}
