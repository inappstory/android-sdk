package com.inappstory.sdk.stories.uidomain.reader.page;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public final class StoriesReaderPageViewModel implements IStoriesReaderPageViewModel {
    public StoriesReaderPageViewModel(StoriesReaderPageState initState) {
        this.state = initState;
    }

    private final StoriesReaderPageState state;

    public StoriesReaderPageState getState() {
        return state;
    }

    private final BottomPanelViewModel bottomPanelViewModel = new BottomPanelViewModel();

    public BottomPanelViewModel getBottomPanelViewModel() {
        return bottomPanelViewModel;
    }

    private final MutableLiveData<Integer> currentSlide = new MutableLiveData<>(0);

    public void updateCurrentSlide(int currentSlide) {
        this.currentSlide.postValue(currentSlide);
    }

    public LiveData<Integer> currentSlideLD() {
        return currentSlide;
    }

    private final MutableLiveData<Boolean> isActive = new MutableLiveData<>(false);

    public void updateIsActive(boolean isActive) {
        this.isActive.postValue(isActive);
    }

    public LiveData<Boolean> isActiveLD() {
        return isActive;
    }
}
