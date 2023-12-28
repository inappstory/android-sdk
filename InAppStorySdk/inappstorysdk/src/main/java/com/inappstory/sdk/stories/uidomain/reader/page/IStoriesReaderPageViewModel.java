package com.inappstory.sdk.stories.uidomain.reader.page;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.IStoryTimelineManager;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.IBottomPanelViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.IStoriesDisplayViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay.SlideLoadState;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallbackWithContext;

public interface IStoriesReaderPageViewModel {
    StoriesReaderPageState getState();

    IBottomPanelViewModel getBottomPanelViewModel();

    IStoryTimelineManager getTimelineManager();

    void cacheStoryLoaded();

    String viewModelUUID();

    void cacheSlideLoaded(int slideIndex);

    void jsSlideLoaded(int slideIndex);

    void jsSlideStarted();


    void startSlide();
    void stopSlide();
    void pauseSlide(boolean moveToBackground);
    void resumeSlide(boolean returnFromBackground);

    IStoriesDisplayViewModel displayViewModel();

    void updateCurrentSlide(int currentSlide);

    void restartSlide(long duration);

    void changeCurrentSlideIndex(int currentSlide);

    LiveData<Integer> currentSlideLD();

    LiveData<IStoryDTO> storyModel();

    void updateIsActive(boolean isActive);

    LiveData<Boolean> isActiveLD();

    LiveData<SlideLoadState> slideLoadState();

    LiveData<IUseCaseCallbackWithContext> eventWithContext();

    void shareClick();

    void likeClick();

    void dislikeClick();

    void favoriteClick();

    void soundClick();

    void destroy();

    void openNextSlide();

    void openNextSlideByTimer();

    void openPrevSlide();

    void clickWithPayload(String payload);
}
