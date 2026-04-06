package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import android.webkit.JavascriptInterface;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderButtonsState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageState;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;

public class StoryReaderPageViewModel {
    private final IASCore core;
    private final StoryReaderViewModel readerViewModel;

    private final Observable<StoryReaderPageLoaderState> storyReaderPageLoaderStateObservable =
            new Observable<>(new StoryReaderPageLoaderState());

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


    @JavascriptInterface
    public void storyClick(String payload) { //page
        manager.slideClick(payload);
        logMethod(payload);
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
        manager.writeToClipboard(payload);
        logMethod(payload);
    }

    @JavascriptInterface
    public void vibrate(int[] vibratePattern) {
        manager.vibrate(vibratePattern);
    } //common


    @JavascriptInterface
    public void storyFreezeUI() { //reader
        manager.freezeUI();
        logMethod("");
    }


    @JavascriptInterface
    public void storyRenderReady() { //page
        manager.renderReady();
        logMethod("");
    }


    @JavascriptInterface
    public void storyUnfreezeUI() { //reader
        manager.unfreezeUI();
        logMethod("");
    }

    @JavascriptInterface
    public void storyShowSlide(int index) { //page
        if (manager.index != index) {
            manager.changeIndex(index);
        }
        logMethod("" + index);
    }

    @JavascriptInterface
    public void showSingleStory(int id, int index) { // page/reader/common
        logMethod("" + id + " " + index);
        if (manager.storyId != id) {
            manager.showSingleStory(id, index);
        } else if (manager.index != index) {
            manager.changeIndex(index);
        }
    }

    @JavascriptInterface
    public void sendApiRequest(String data) {
        manager.sendApiRequest(data);
    } //common?/page


    @JavascriptInterface
    public void openGame(String gameInstanceId) { //common
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
