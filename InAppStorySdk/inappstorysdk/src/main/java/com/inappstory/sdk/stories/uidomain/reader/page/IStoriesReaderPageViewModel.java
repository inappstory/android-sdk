package com.inappstory.sdk.stories.uidomain.reader.page;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.IStoryTimelineManager;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.IBottomPanelViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.IStoriesDisplayViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.SlideContentState;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.SlideLoadState;

public interface IStoriesReaderPageViewModel {
    StoriesReaderPageState getState();

    IBottomPanelViewModel getBottomPanelViewModel();

    IStoryTimelineManager getTimelineManager();

    void storyLoaded();

    IStoriesDisplayViewModel displayViewModel();

    void updateCurrentSlide(int currentSlide);

    LiveData<Integer> currentSlideLD();

    LiveData<IStoryDTO> storyModel();

    void updateIsActive(boolean isActive);

    LiveData<Boolean> isActiveLD();

    LiveData<SlideLoadState> slideLoadState();

    void shareClick();

    void likeClick();

    void dislikeClick();

    void favoriteClick();

    void soundClick();
}
