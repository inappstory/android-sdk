package com.inappstory.sdk.stories.uidomain.reader.page;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.IStoryTimelineManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimelineManager;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.BottomPanelViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.IStoriesDisplayViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.StoriesWebViewDisplayViewModel;
import com.inappstory.sdk.utils.ArrayUtil;

public final class StoriesReaderPageViewModel implements IStoriesReaderPageViewModel {
    public StoriesReaderPageViewModel(StoriesReaderPageState initState) {
        this.state = initState;
        bottomPanelViewModel = new BottomPanelViewModel(this);
        storiesDisplayViewModel = new StoriesWebViewDisplayViewModel(this);
        timelineManager = new StoryTimelineManager();
        IStoryDTO storyDTO = IASCore.getInstance().getStoriesRepository(
                initState.getStoryType()).getStoryById(
                        initState.storyId()
        );
        setStoryModel(storyDTO);
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

    public BottomPanelViewModel getBottomPanelViewModel() {
        return bottomPanelViewModel;
    }

    @Override
    public IStoryTimelineManager getTimelineManager() {
        return timelineManager;
    }

    @Override
    public void storyLoaded() {
        IStoryDTO storyDTO = IASCore.getInstance().getStoriesRepository(
                state.getStoryType()).getStoryById(
                state.storyId()
        );
        setStoryModel(storyDTO);
    }

    @Override
    public IStoriesDisplayViewModel displayViewModel() {
        return storiesDisplayViewModel;
    }

    private final MutableLiveData<Integer> currentSlide = new MutableLiveData<>(0);

    public void updateCurrentSlide(int currentSlide) {
        this.currentSlide.postValue(currentSlide);
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
}
