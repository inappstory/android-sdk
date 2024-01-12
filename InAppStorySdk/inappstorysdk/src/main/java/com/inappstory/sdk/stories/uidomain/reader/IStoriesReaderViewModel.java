package com.inappstory.sdk.stories.uidomain.reader;

import androidx.lifecycle.LiveData;

import com.inappstory.sdk.inputdialog.uidomain.IInputDialogViewModel;
import com.inappstory.sdk.stories.outercallbacks.common.objects.ShowStoryAction;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;

import java.util.List;

public interface IStoriesReaderViewModel {
    IStoriesReaderPageViewModel getPageViewModel(int index);
    IStoriesReaderPageViewModel getPageViewModelByStoryId(int storyId);
    IStoriesReaderPageViewModel getLaunchedViewModel();
    IInputDialogViewModel getDialogViewModel();
    List<IStoriesReaderPageViewModel> getPageViewModels();
    void initNewState(StoriesReaderState state);
    StoriesReaderState getState();
    LiveData<Boolean> isOpened();
    LiveData<Boolean> isOpenAnimation();
    LiveData<Boolean> frozen();
    void changeFreezeStatus(boolean frozen);
    LiveData<Boolean> isPagerAnimation();
    LiveData<Integer> currentIndex();
    void currentIndex(int index);
    void nextStory(ShowStoryAction action);
    void prevStory(ShowStoryAction action);
    void pagerAnimation(boolean animated);
    void pauseCurrentSlide(boolean moveToBackground);
    void resumeCurrentSlide(boolean returnFromBackground);
    void openAnimationStatus(boolean animated);
    void isOpened(boolean isOpened);
    void clear();
}
