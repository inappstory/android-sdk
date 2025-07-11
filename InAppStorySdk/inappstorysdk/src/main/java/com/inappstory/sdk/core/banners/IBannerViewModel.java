package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

public interface IBannerViewModel extends IReaderSlideViewModel {
    BannerState getCurrentBannerState();
    BannerData getCurrentBannerData();


    SingleTimeEvent<STETypeAndData> singleTimeEvents();

    void addSubscriber(Observer<BannerState> observer);
    void removeSubscriber(Observer<BannerState> observer);
    void updateCurrentLoadState(BannerLoadStates bannerLoadState);

    void pauseSlide();
    void resumeSlide();

    void stopSlide();

    void iterationId(String iterationId);


    void updateTimeline(String data);
    void slideClick(String payload);
    void slideLoadingFailed(String data);
    void showSingleStory(int id, int index);
    void sendApiRequest(String data);
    void vibrate(int[] vibratePattern);
    void openGame(String gameInstanceId);
    void setAudioManagerMode(String mode);
    void slideStarted(Double startTime);
    void slideLoaded(String data);
    void statisticEvent(
            String name,
            String data,
            String eventData
    );
    void share(String id, String data);
    void freezeUI();
    void unfreezeUI();
    void sendData(String data);
    void setLocalUserData(String data, boolean sendToServer);
    String getLocalUserData();
    void shareSlideScreenshotCb(String shareId, boolean result);


    void bannerIsShown();
    boolean bannerIsActive();
    void bannerIsActive(boolean active);
}
