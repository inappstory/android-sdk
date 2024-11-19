package com.inappstory.sdk.inappmessage.domain.reader;


import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.core.inappmessages.InAppMessageDownloadManager;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.SlideLinkObject;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.callbackdata.CallToActionData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.WebPageConvertCallback;
import com.inappstory.sdk.stories.utils.WebPageConverter;

public class IAMReaderSlideViewModel implements IIAMReaderSlideViewModel {
    private final Observable<IAMReaderSlideState> slideStateObservable =
            new Observable<>(new IAMReaderSlideState());


    public SingleTimeEvent<CallToActionData> callToActionDataSTE() {
        return callToActionDataSTE;
    }

    private final SingleTimeEvent<CallToActionData> callToActionDataSTE =
            new SingleTimeEvent<>();

    private final IAMReaderViewModel readerViewModel;
    private final IASCore core;

    public IAMReaderSlideViewModel(
            IAMReaderViewModel readerViewModel,
            IASCore core
    ) {
        this.readerViewModel = readerViewModel;
        this.core = core;
    }

    @Override
    public void addSubscriber(Observer<IAMReaderSlideState> observable) {
        this.slideStateObservable.subscribe(observable);
    }

    @Override
    public void removeSubscriber(Observer<IAMReaderSlideState> observable) {
        this.slideStateObservable.unsubscribe(observable);
    }



    @Override
    public ContentIdWithIndex iamId() {
        Integer iamId = readerViewModel.getCurrentState().iamId;
        if (iamId != null) {
            return new ContentIdWithIndex(iamId, 0);
        }
        return null;
    }

    public void slideClick(String payload) {
        if (payload != null && !payload.isEmpty()) {

            Log.e("IASClickPayload", payload);
            final SlideLinkObject object = JsonParser.fromJson(payload, SlideLinkObject.class);
            if (object != null) {
                ClickAction action = ClickAction.BUTTON;
                if (object.getLink().getType().equals("url")) {
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        if ("swipeUpLink".equals(object.getType())) {
                            action = ClickAction.SWIPE;
                        }
                    }
                    final ClickAction finalAction = action;
                    callToActionDataSTE.updateValue(
                            new CallToActionData()
                                    .slideData(null)
                                    .link(object.getLink().getTarget())
                                    .clickAction(finalAction)
                    );
                }
            }
        }
    }


    public void updateTimeline(String data) {
//        throw new NotImplementedMethodException();
    }

    public void storyLoadingFailed(String data) {
        throw new NotImplementedMethodException();
    }

    public void storyShowSlide(int index) {
        throw new NotImplementedMethodException();
    }

    public void showSingleStory(int id, int index) {
        throw new NotImplementedMethodException();
    }

    public void sendApiRequest(String data) {
        throw new NotImplementedMethodException();
    }

    public void vibrate(int[] vibratePattern) {
        throw new NotImplementedMethodException();
    }

    public void openGame(String gameInstanceId) {
        throw new NotImplementedMethodException();
    }

    public void setAudioManagerMode(String mode) {
        throw new NotImplementedMethodException();
    }

    public void storyShowNext() {
        throw new NotImplementedMethodException();
    }

    public void storyShowPrev() {
        throw new NotImplementedMethodException();
    }

    public void storyShowNextSlide(long delay) {
        throw new NotImplementedMethodException();
    }

    public void storyShowNextSlide() {
        throw new NotImplementedMethodException();
    }

    public void storyShowTextInput(String id, String data) {
        throw new NotImplementedMethodException();
    }

    public void storyStarted() {
    }

    public void storyStarted(double startTime) {
        throw new NotImplementedMethodException();
    }

    public void storyLoaded() {
        readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.LOADED);
    }

    public void storyLoaded(String data) {
        readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.LOADED);
    }

    public void storyStatisticEvent(
            String name,
            String data,
            String eventData
    ) {

    }

    public void storyStatisticEvent(
            String name,
            String data
    ) {
        throw new NotImplementedMethodException();
    }

    public void emptyLoaded() {
        throw new NotImplementedMethodException();
    }

    public void share(String id, String data) {
        throw new NotImplementedMethodException();
    }

    public void storyFreezeUI() {
        throw new NotImplementedMethodException();
    }

    public void storySendData(String data) {
        throw new NotImplementedMethodException();
    }

    public void storySetLocalData(String data, boolean sendToServer) {
        synchronized (localDataLock) {
            core.keyValueStorage().saveString("iam" +
                    readerViewModel.getCurrentState().iamId + "__" +
                    ((IASDataSettingsHolder) core.settingsAPI()).userId(), data);
        }
    }

    private final Object localDataLock = new Object();

    public String storyGetLocalData() {
        synchronized (localDataLock) {
            String res = core.keyValueStorage().getString("iam" +
                    readerViewModel.getCurrentState().iamId
                    + "__" + ((IASDataSettingsHolder) core.settingsAPI()).userId());
            return res == null ? "" : res;
        }
    }

    public void shareSlideScreenshotCb(String shareId, boolean result) {
        throw new NotImplementedMethodException();
    }

    public void defaultTap(String val) {
        throw new NotImplementedMethodException();
    }

    @Override
    public ContentIdAndType contentIdAndType() {
        Integer iamId = readerViewModel.getCurrentState().iamId;
        if (iamId != null) {
            return new ContentIdAndType(iamId, ContentType.IN_APP_MESSAGE);
        }
        return null;
    }

    @Override
    public void contentLoadError() {

    }

    @Override
    public void slideLoadError(int index) {
        readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.FAILED);
        slideStateObservable.updateValue(
                slideStateObservable.getValue().copy().contentStatus(-1).content(null)
        );
    }

    @Override
    public void contentLoadSuccess(IReaderContent content) {

    }

    @Override
    public void slideLoadSuccess(int index) {
        InAppMessageDownloadManager downloadManager = core.contentLoader().inAppMessageDownloadManager();
        downloadManager.removeSubscriber(this);
        IAMReaderState readerState = readerViewModel.getCurrentState();
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(
                        readerState.iamId,
                        ContentType.IN_APP_MESSAGE
                );
        if (readerContent == null) return;
        String slideContent = readerContent.slideByIndex(0);
        if (slideContent == null) return;
        WebPageConvertCallback callback = new WebPageConvertCallback() {
            @Override
            public void onConvert(String replaceData, String firstData, int lastIndex) {
                slideStateObservable.updateValue(
                        slideStateObservable
                                .getValue()
                                .copy()
                                .contentStatus(1)
                                .content(firstData)
                );
            }
        };
        WebPageConverter converter = new WebPageConverter();
        converter.replaceDataAndLoad(slideContent, readerContent, index, callback);
    }

    @Override
    public void loadContent() {
        IAMReaderState state = readerViewModel.getCurrentState();
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(
                        state.iamId,
                        ContentType.IN_APP_MESSAGE
                );
        InAppMessageDownloadManager downloadManager = core.contentLoader().inAppMessageDownloadManager();
        downloadManager.addSubscriber(this);
        if (downloadManager.allSlidesLoaded(readerContent)) {
            if (!state.showOnlyIfLoaded) {
                readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.LOADING);
            }
            if (!downloadManager.checkBundleResources(this, state.showOnlyIfLoaded)) {
                readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.FAILED);
            }
        } else {
            if (state.showOnlyIfLoaded) {
                readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.FAILED);
            } else {
                readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.LOADING);
                downloadManager.addInAppMessageTask(state.iamId, ContentType.IN_APP_MESSAGE);
            }
        }
    }
}
