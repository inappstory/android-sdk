package com.inappstory.sdk.stories.uidomain.reader.page;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.IBottomPanelViewModel;

public interface IStoriesReaderPageViewModel {
    StoriesReaderPageState getState();

    IBottomPanelViewModel getBottomPanelViewModel();

    void updateCurrentSlide(int currentSlide);

    LiveData<Integer> currentSlideLD();

    void updateIsActive(boolean isActive);

    LiveData<Boolean> isActiveLD();

    void shareClick();

    void likeClick();

    void dislikeClick();

    void favoriteClick();

    void soundClick();
}
