package com.inappstory.sdk.stories.uidomain.reader.page;

import androidx.lifecycle.LiveData;

public interface IStoriesReaderPageViewModel {
    StoriesReaderPageState getState();

    IBottomPanelViewModel getBottomPanelViewModel();

    void updateCurrentSlide(int currentSlide);

    LiveData<Integer> currentSlideLD();

    void updateIsActive(boolean isActive);

    LiveData<Boolean> isActiveLD();
}
