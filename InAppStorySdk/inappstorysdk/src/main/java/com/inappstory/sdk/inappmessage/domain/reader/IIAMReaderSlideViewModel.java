package com.inappstory.sdk.inappmessage.domain.reader;


import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.outercallbacks.common.reader.callbackdata.CallToActionData;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

public interface IIAMReaderSlideViewModel extends IReaderSlideViewModel {
    void addSubscriber(Observer<IAMReaderSlideState> observer);
    void removeSubscriber(Observer<IAMReaderSlideState> observer);

    SingleTimeEvent<CallToActionData> callToActionDataSTE();

    ContentIdWithIndex iamId();

    void slideClick(String payload);

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

    void storyShowNextSlide(long delay);

    void storyShowNextSlide();

    void storyShowTextInput(String id, String data);

    void storyStarted();

    void storyStarted(double startTime);

    void storyLoaded();

    void storyLoaded(String data);

    void storyStatisticEvent(
            String name,
            String data,
            String eventData
    );

    void storyStatisticEvent(
            String name,
            String data
    );

    void emptyLoaded();

    void share(String id, String data);

    void storyFreezeUI();

    void storySendData(String data);

    void storySetLocalData(String data, boolean sendToServer);

    String storyGetLocalData();

    void shareSlideScreenshotCb(String shareId, boolean result);

    void defaultTap(String val);
}
