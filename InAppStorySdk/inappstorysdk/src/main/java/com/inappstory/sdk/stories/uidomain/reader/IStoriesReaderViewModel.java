package com.inappstory.sdk.stories.uidomain.reader;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;

public interface IStoriesReaderViewModel {
    IStoriesReaderPageViewModel getPageViewModel(int index);
    IStoriesReaderPageViewModel getPageViewModelByStoryId(int storyId);
    IStoriesReaderPageViewModel getLaunchedViewModel();
    void initNewState(StoriesReaderState state);
    StoriesReaderState getState();
    LiveData<Boolean> isOpened();
    LiveData<Boolean> isOpenAnimation();
    LiveData<Boolean> isPagerAnimation();
    LiveData<Integer> currentIndex();
    void currentIndex(int index);
    void pagerAnimation(boolean animated);
    void openAnimationStatus(boolean animated);
    void isOpened(boolean isOpened);
}
