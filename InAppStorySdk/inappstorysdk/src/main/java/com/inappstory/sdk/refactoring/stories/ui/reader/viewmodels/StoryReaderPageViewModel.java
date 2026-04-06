package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.CancellationTokenImpl;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.inappmessage.domain.stedata.JsSendApiRequestData;
import com.inappstory.sdk.inappmessage.domain.stedata.STEDataType;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.JsSendApiRequestResponse;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderButtonsState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageState;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;
import com.inappstory.sdk.utils.ClipboardUtils;

import java.util.Objects;

public class StoryReaderPageViewModel {
    private final IASCore core;
    private final StoryReaderViewModel readerViewModel;

    private final Observable<StoryReaderPageLoaderState> storyReaderPageLoaderStateObservable =
            new Observable<>(new StoryReaderPageLoaderState());

    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
            new SingleTimeEvent<>();

    private final Observable<StoryReaderPageState> storyReaderPageStateObservable;

    private final Observable<StoryReaderButtonsState> storyReaderPageButtonsStateObservable =
            new Observable<>(new StoryReaderButtonsState());

    public StoryReaderPageViewModel(
            IASCore core,
            StoryReaderViewModel readerViewModel,
            String storyId,
            int pageIndex
    ) {
        this.core = core;
        this.readerViewModel = readerViewModel;
        storyReaderPageStateObservable =
                new Observable<>(new StoryReaderPageState(storyId, pageIndex));
    }

    public void addLoaderStateSubscriber(Observer<StoryReaderPageLoaderState> observer) {
        storyReaderPageLoaderStateObservable.subscribeAndGetValue(observer);
    }

    public void removeLoaderStateSubscriber(Observer<StoryReaderPageLoaderState> observer) {
        storyReaderPageLoaderStateObservable.unsubscribe(observer);
    }

    public void addPageStateSubscriber(Observer<StoryReaderPageState> observer) {
        storyReaderPageStateObservable.subscribeAndGetValue(observer);
    }

    public void removePageStateSubscriber(Observer<StoryReaderPageState> observer) {
        storyReaderPageStateObservable.unsubscribe(observer);
    }

    public void addButtonsStateSubscriber(Observer<StoryReaderButtonsState> observer) {
        storyReaderPageButtonsStateObservable.subscribeAndGetValue(observer);
    }

    public void removeButtonsStateSubscriber(Observer<StoryReaderButtonsState> observer) {
        storyReaderPageButtonsStateObservable.unsubscribe(observer);
    }

    public void updateLatestClickCoordinates(float coordinate) {
        clickCoordinates = (int) coordinate;
    }

    private int clickCoordinates = -1;

    private int getClickCoordinates() {
        int resCoordinates = clickCoordinates;
        clickCoordinates = -1;
        return resCoordinates;
    }

    public void clickOnRefresh() {
    }

    public void likeClick() {
    }

    public void dislikeClick() {
    }

    public void favoriteClick() {
    }

    public void soundClick() {
    }

    public void shareClick() {
    }

    private void navigate(int coordinate, boolean forbidden) {

    }

    private void handleClickPayload(String payload) {

    }

    @JavascriptInterface
    public void storyClick(String payload) { //page
        int coordinate = getClickCoordinates();
        if (payload == null || payload.isEmpty() || payload.equals("test")) {
            navigate(coordinate, false);
        } else if (payload.equals("forbidden")) {
            navigate(coordinate, true);
        } else {
            handleClickPayload(payload);
        }
    }

    @JavascriptInterface
    public void updateTimeline(String data) { //page
        if (data != null) {
            UpdateTimelineData updateTimelineData = JsonParser.fromJson(data, UpdateTimelineData.class);
            manager.updateTimeline(updateTimelineData);
        }
        logMethod(data);
    }

    @JavascriptInterface
    public void storyLoadingFailed(String data) { //page
        if (data != null) {
            StoryLoadedData loadedData = JsonParser.fromJson(data, StoryLoadedData.class);
            manager.slideLoadError(loadedData.index);
        }
        logMethod("");
    }

    @JavascriptInterface
    public void writeToClipboard(String payload) { //common
        ClipboardUtils.writeToClipboard(payload, core.appContext());
    }

    @JavascriptInterface
    public void vibrate(int[] vibratePattern) {
        core.vibrateUtils().vibrate(vibratePattern);
    }


    @JavascriptInterface
    public void storyFreezeUI() {
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        STEDataType.FREEZE_UI,
                        null
                )
        );
    }


    @JavascriptInterface
    public void storyRenderReady() {
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        STEDataType.RENDER_READY,
                        null
                )
        );
    }


    @JavascriptInterface
    public void storyUnfreezeUI() { //reader
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        STEDataType.UNFREEZE_UI,
                        null
                )
        );
    }

    @JavascriptInterface
    public void storyShowSlide(int index) { //page
        StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
        if (pageState.slideIndex() != index) {
            storyReaderPageStateObservable.updateValue(pageState.copy().slideIndex(index));
        }
    }

    public void openAnotherStory(Context context, int id, int index) {
        try {
            AppearanceManager appearanceManager = AppearanceManager.checkOrCreateAppearanceManager(null);
            ((IASSingleStoryImpl) core.singleStoryAPI()).show(
                    new CancellationTokenImpl(),
                    context,
                    Integer.toString(id),
                    appearanceManager,
                    null,
                    index,
                    true,
                    SourceType.SINGLE,
                    ShowStory.ACTION_CUSTOM
            );
        } catch (Exception e) {

        }
    }

    public void openGame(Context context, String gameInstanceId) {
        try {
            if (core.gamesAPI().gameCanBeOpened(gameInstanceId)) {
                core.screensManager().openScreen(
                        context,
                        new LaunchGameScreenStrategy(core, true)
                                .data(new LaunchGameScreenData(
                                        null,
                                        readerViewModel.getCurrentInAppMessageData(),
                                        gameInstanceId
                                ))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void showSingleStory(int id, int index) {

        StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
        if (!Objects.equals(pageState.storyId(), Integer.toString(id))) {
            singleTimeEvents.updateValue(
                    new STETypeAndData(STEDataType.OPEN_STORY,
                            new ContentIdWithIndex(id, index)
                    )
            );
        } else if (pageState.slideIndex() != index) {
            storyReaderPageStateObservable.updateValue(pageState.copy().slideIndex(index));
        }
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        new JsApiClient(
                core,
                core.appContext(),
                core.projectSettingsAPI().host()
        ).sendApiRequest(data, new JsApiResponseCallback() {
            @Override
            public void onJsApiResponse(String result, String cb) {
                singleTimeEvents.updateValue(
                        new STETypeAndData(STEDataType.JS_SEND_API_RESPONSE,
                                new JsSendApiRequestResponse()
                                        .cb(cb)
                                        .result(result)
                        )
                );
            }
        });

    }


    @JavascriptInterface
    public void openGame(String gameInstanceId) {

        manager.openGameReaderFromGameCenter(gameInstanceId);
        logMethod(gameInstanceId);
    }

    @JavascriptInterface
    public void setAudioManagerMode(String mode) { //common
        manager.setAudioManagerMode(mode);
        logMethod(mode);
    }


    @JavascriptInterface
    public void storyShowNext() { //reader
        manager.storyShowNext();
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowPrev() { //reader
        manager.storyShowPrev();
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowNextSlide(long delay) { //page
        if (delay == 0) {
            manager.changeIndex(manager.index + 1);
        }
        logMethod("" + delay);
    }

    @JavascriptInterface
    public void storyShowNextSlide() { //page
        manager.changeIndex(manager.index + 1);
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowTextInput(String id, String data) { // page/reader?
        manager.storyShowTextInput(id, data);
        logMethod("");
    }

    @JavascriptInterface
    public void storyStarted() { //page
        manager.storyStartedEvent();
        manager.pageFinished();
        logMethod("");
    }

    @JavascriptInterface
    public void storyStarted(double startTime) { //page
        manager.storyStartedEvent();
        manager.pageFinished();
        logMethod("" + startTime);
    }

    @JavascriptInterface
    public void storyLoaded() { //page
        manager.storyLoaded(-1);
        logMethod("");
    }

    @JavascriptInterface
    public void storyLoaded(String data) { //page
        if (data != null) {
            int slideIndex = JsonParser.fromJson(data, StoryLoadedData.class).index;
            manager.storyLoaded(slideIndex);
        } else {
            manager.storyLoaded(-1);
        }
        logMethod(data + "");
    }


    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data,
            String eventData
    ) { //page
        manager.sendStoryWidgetEvent(name, data, eventData, false);
        logMethod(name + " " + data + " " + eventData);
    }

    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data,
            String eventData,
            boolean forceEnableStatisticV2
    ) { //page
        manager.sendStoryWidgetEvent(name, data, eventData, forceEnableStatisticV2);
        logMethod(name + " " + data + " " + eventData + " " + forceEnableStatisticV2);
    }

    @JavascriptInterface
    public void emptyLoaded() {
        logMethod("");
    }

    @JavascriptInterface
    public void share(String id, String data) { //page
        manager.share(id, data);
        logMethod(id + " " + data);
    }


    @JavascriptInterface
    public void disableVerticalSwipeGesture() { // page/reader
        manager.swipeVerticalGestureEnabled(false);
        logMethod("");
    }

    @JavascriptInterface
    public void enableVerticalSwipeGesture() { // page/reader
        manager.swipeVerticalGestureEnabled(true);
        logMethod("");
    }

    @JavascriptInterface
    public void disableBackpress() { // reader
        manager.backPressEnabled(false);
        logMethod("");
    }

    @JavascriptInterface
    public void enableBackpress() { // reader
        manager.backPressEnabled(true);
        logMethod("");
    }

    @JavascriptInterface
    public void storySendData(String data) { //page
        manager.storySendData(data);
        logMethod(data);
    }

    @JavascriptInterface
    public void storySetLocalData(String data, boolean sendToServer) { //page
        synchronized (lock) {
            manager.storySetLocalData(data, sendToServer);
            logMethod(data + " " + sendToServer);
        }
    }

    @JavascriptInterface
    public void closeStory(String reason) { //reader
        manager.closeStory(reason.toLowerCase());
        logMethod(reason);
    }

    @JavascriptInterface
    public String storyGetLocalData() {  //page
        synchronized (localDataLock) {
            String res = core.keyValueStorage().getString("story" + manager.storyId
                    + "__" + ((IASDataSettingsHolder) core.settingsAPI()).userId());
            logMethod(res != null ? res : "");
            return res == null ? "" : res;
        }
    }

    private final Object localDataLock = new Object();

    @JavascriptInterface
    public void shareSlideScreenshotCb(String shareId, boolean result) {  //page
        manager.screenshotShareCallback(shareId);
    }

    @JavascriptInterface
    public void productCartUpdate(String productCartData, String callbacks) { //page?
        manager.productCartUpdate(productCartData, callbacks);
    }

    @JavascriptInterface
    public void productCartClicked() {
        manager.productCartClicked();
    } //page?

    @JavascriptInterface
    public void productCartGetState(String callbacks) {
        manager.productCartGetState(callbacks);
    }
}
