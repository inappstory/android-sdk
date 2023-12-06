package com.inappstory.sdk.stories.uidomain.reader;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;

public interface IStoriesReaderViewModel {
    IStoriesReaderPageViewModel getPageViewModel(int index);
    void initNewState(StoriesReaderState state);
    StoriesReaderState getState();
    LiveData<Boolean> isOpened();
    LiveData<Boolean> isOpenCloseAnimation();
    LiveData<Boolean> isPagerAnimation();
    LiveData<Integer> currentIndex();
    void currentIndex(int index);
    void pagerAnimation(boolean animated);
    void openCloseAnimation(boolean animated);
    void isOpened(boolean isOpened);
}
