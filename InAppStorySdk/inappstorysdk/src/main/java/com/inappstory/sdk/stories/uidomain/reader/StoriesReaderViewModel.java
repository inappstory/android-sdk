package com.inappstory.sdk.stories.uidomain.reader;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;
import com.inappstory.sdk.stories.uidomain.reader.page.StoriesReaderPageState;
import com.inappstory.sdk.stories.uidomain.reader.page.StoriesReaderPageViewModel;

import java.util.ArrayList;
import java.util.List;

public final class StoriesReaderViewModel implements IStoriesReaderViewModel {
    public StoriesReaderState getState() {
        return state;
    }

    @Override
    public LiveData<Boolean> isOpened() {
        return isOpened;
    }

    @Override
    public LiveData<Boolean> isOpenCloseAnimation() {
        return openCloseAnimation;
    }

    @Override
    public LiveData<Boolean> isPagerAnimation() {
        return pagerAnimation;
    }

    @Override
    public LiveData<Integer> currentIndex() {
        return currentIndex;
    }

    @Override
    public void currentIndex(int index) {
        currentIndex.postValue(index);
        for (int i = 0; i < pageViewModels.size(); i++) {
            pageViewModels.get(i).updateIsActive(index == i);
        }
    }

    @Override
    public void pagerAnimation(boolean animated) {
        this.pagerAnimation.postValue(animated);
    }

    @Override
    public void openCloseAnimation(boolean animated) {
        this.openCloseAnimation.postValue(animated);
    }

    @Override
    public void isOpened(boolean isOpened) {
        this.isOpened.postValue(isOpened);
    }

    private StoriesReaderState state;

    private final MutableLiveData<Boolean> isOpened = new MutableLiveData<>();
    private final MutableLiveData<Boolean> pagerAnimation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> openCloseAnimation = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>(0);

    private List<IStoriesReaderPageViewModel> pageViewModels = new ArrayList<>();

    public IStoriesReaderPageViewModel getPageViewModel(int index) {
        if (pageViewModels.size() <= index) return null;
        return pageViewModels.get(index);
    }

    public void initNewState(@NonNull StoriesReaderState state) {
        this.state = state;
        pageViewModels.clear();
        List<Integer> ids = state.launchData().getStoriesIds();
        for (int i = 0; i < ids.size(); i++) {
            int currentSlide = 0;
            Integer currentStoryOpenIndex = state.launchData().getSlideIndex();
            if (i == state.launchData().getListIndex() && currentStoryOpenIndex != null) {
                currentSlide = currentStoryOpenIndex;
            }
            IPreviewStoryDTO previewStoryDTO = IASCore.getInstance().getStoriesRepository(
                    state.launchData().getType()).getStoryPreviewById(ids.get(i));
            StoriesReaderPageViewModel pageViewModel = new StoriesReaderPageViewModel(
                    previewStoryDTO != null ?
                            new StoriesReaderPageState(
                                    state.appearanceSettings(),
                                    ids.get(i),
                                    previewStoryDTO.hasSwipeUp(),
                                    previewStoryDTO.disableClose()
                            ) :
                            new StoriesReaderPageState(
                                    state.appearanceSettings(),
                                    ids.get(i)
                            )
            );
            pageViewModel.updateCurrentSlide(currentSlide);
            pageViewModels.add(pageViewModel);
        }
        currentIndex(state.launchData().getListIndex());
    }
}
