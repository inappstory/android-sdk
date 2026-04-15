package com.inappstory.sdk.inappmessage.domain.reader;


import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.inappmessage.InAppMessageSlideData;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.OnVerticalScrollJSData;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

public interface IIAMReaderSlideViewModel extends IReaderSlideViewModel {
    void reloadContent();

    void addSubscriber(Observer<IAMReaderSlideState> observer);

    void removeSubscriber(Observer<IAMReaderSlideState> observer);

    void addScrollSubscriber(Observer<IAMReaderScrollState> observer);

    void removeScrollSubscriber(Observer<IAMReaderScrollState> observer);

    void readerIsOpened(boolean fromScratch);

    void readerIsClosing();

    void closeReader();

    void updateLayout();

    void onVerticalScrollChange(OnVerticalScrollJSData scrollData);

    void onCardLoadingStateChange(int state, String reason);

    void onEvent(String name, String event);

    SingleTimeEvent<STETypeAndData> singleTimeEvents();

    ContentIdWithIndex iamId();

    InAppMessageSlideData slideData();

    void slideClick(String payload);

    void resumeSlideTimer();

    void clear();

    void updateTimeline(String data);

    void storyLoadingFailed(String data);

    void storyShowSlide(int index);

    void showSingleStory(int id, int index);

    void sendApiRequest(String data);

    void vibrate(int[] vibratePattern);

    void openGame(String gameInstanceId);

    void setAudioManagerMode(String mode);

    void storyShowNext();

    void storyShowPrev();

    void writeToClipboard(String payload);

    void storyShowNextSlide(long delay);

    void storyShowNextSlide();

    void storyShowTextInput(String id, String data);

    void storyStarted();

    void storyStarted(double startTime);

    void storyLoaded();

    void storyLoaded(String data);

    void statisticEvent(
            String name,
            String data,
            String eventData
    );

    void emptyLoaded();

    void share(String id, String data);

    void storyFreezeUI();

    void storyUnfreezeUI();

    void storySendData(String data);

    void setLocalUserData(String data, boolean sendToServer);

    String getLocalUserData();

    void shareSlideScreenshotCb(String shareId, boolean result);

    void enableVerticalSwipeGesture();

    void disableVerticalSwipeGesture();

    void defaultTap(String val);
}
