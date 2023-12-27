package com.inappstory.sdk.stories.uidomain.reader;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.models.api.Story;
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
    public LiveData<Boolean> isOpenAnimation() {
        return openAnimation;
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
        if (pageViewModels.size() <= index || index < 0) return;
        currentIndex.postValue(index);
        for (int i = 0; i < pageViewModels.size(); i++) {
            pageViewModels.get(i).updateIsActive(index == i);
        }
        downloadStory(index);
    }

    private int index() {
        Integer index = currentIndex.getValue();
        if (index == null) return 0;
        return index;
    }

    @Override
    public void nextStory() {
        currentIndex(index() + 1);
    }

    @Override
    public void prevStory() {
        currentIndex(index() - 1);
    }

    private void downloadStory(int index) {
        ArrayList<Integer> adds = new ArrayList<>();
        Story.StoryType storyType = state.launchData().getType();
        List<Integer> storiesIds = state.launchData().getStoriesIds();
        if (storiesIds.size() > 1) {
            if (index == 0) {
                adds.add(storiesIds.get(index + 1));
            } else if (index == storiesIds.size() - 1) {
                adds.add(storiesIds.get(index - 1));
            } else {
                adds.add(storiesIds.get(index + 1));
                adds.add(storiesIds.get(index - 1));
            }
        }
        IASCore.getInstance().downloadManager.addStoryTask(
                storiesIds.get(index),
                adds,
                storyType
        );
        IASCore.getInstance().downloadManager.changePriority(
                storiesIds.get(index),
                adds,
                storyType
        );
    }

    @Override
    public void pagerAnimation(boolean animated) {
        this.pagerAnimation.postValue(animated);
    }

    @Override
    public void openAnimationStatus(boolean animated) {
        this.openAnimation.postValue(animated);
    }

    @Override
    public void isOpened(boolean isOpened) {
        this.isOpened.postValue(isOpened);
    }

    @Override
    public void clear() {
        isOpened(false);
        for (IStoriesReaderPageViewModel storiesReaderPageViewModel : pageViewModels) {
            storiesReaderPageViewModel.destroy();
        }
        pageViewModels.clear();
        IASCore.getInstance()
                .getStoriesRepository(state.launchData().getType())
                .clearReaderModels();
    }


    private StoriesReaderState state;

    private final MutableLiveData<Boolean> isOpened = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> pagerAnimation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> openAnimation = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>(0);

    private List<IStoriesReaderPageViewModel> pageViewModels = new ArrayList<>();

    @Override
    public IStoriesReaderPageViewModel getPageViewModel(int index) {
        if (pageViewModels.size() <= index) return null;
        return pageViewModels.get(index);
    }

    @Override
    public IStoriesReaderPageViewModel getPageViewModelByStoryId(int storyId) {
        int index = state.launchData().getStoriesIds().indexOf(storyId);
        if (index == -1) return null;
        return getPageViewModel(index);
    }

    @Override
    public IStoriesReaderPageViewModel getLaunchedViewModel() {
        return getPageViewModel(state.launchData().getListIndex());
    }

    @Override
    public List<IStoriesReaderPageViewModel> getPageViewModels() {
        return pageViewModels;
    }

    @Override
    public void initNewState(@NonNull StoriesReaderState state) {
        this.state = state;
        pageViewModels.clear();
        Story.StoryType storyType = state.launchData().getType();
        List<Integer> ids = state.launchData().getStoriesIds();
        for (int i = 0; i < ids.size(); i++) {
            int currentSlide = 0;
            Integer currentStoryOpenIndex = state.launchData().getSlideIndex();
            if (i == state.launchData().getListIndex() && currentStoryOpenIndex != null) {
                currentSlide = currentStoryOpenIndex;
            }
            IPreviewStoryDTO previewStoryDTO = IASCore.getInstance()
                    .getStoriesRepository(storyType)
                    .getStoryPreviewById(ids.get(i));
            StoriesReaderPageViewModel pageViewModel = new StoriesReaderPageViewModel(
                    previewStoryDTO != null ?
                            new StoriesReaderPageState(
                                    state.appearanceSettings(),
                                    ids.get(i),
                                    i,
                                    storyType,
                                    previewStoryDTO.hasSwipeUp(),
                                    previewStoryDTO.disableClose()
                            ) :
                            new StoriesReaderPageState(
                                    state.appearanceSettings(),
                                    ids.get(i),
                                    i,
                                    storyType
                            )
            );
            pageViewModel.updateCurrentSlide(currentSlide);
            pageViewModels.add(pageViewModel);
        }
        currentIndex(state.launchData().getListIndex());
    }
}
