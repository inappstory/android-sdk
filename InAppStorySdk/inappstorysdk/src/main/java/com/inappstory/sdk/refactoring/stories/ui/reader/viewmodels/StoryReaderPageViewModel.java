package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import android.content.Context;
import android.media.AudioManager;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.CancellationTokenImpl;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.data.IStatData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.inappmessage.domain.stedata.JsSendApiRequestData;
import com.inappstory.sdk.inappmessage.domain.stedata.STEDataType;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.JsSendApiRequestResponse;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.StartSlide;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderButtonsState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderImmutableState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderType;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderState;
import com.inappstory.sdk.stories.api.models.ContentId;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.utils.AudioModes;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;
import com.inappstory.sdk.utils.AudioManagerUtils;
import com.inappstory.sdk.utils.ClipboardUtils;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.Map;
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
            int corIndex = correctIndex(index, pageState);
            if (corIndex >= 0)
                storyReaderPageStateObservable.updateValue(pageState.copy().slideIndex(corIndex));
        }
    }

    private int correctIndex(int index, StoryReaderPageState pageState) {
        IStatData storyStatData = pageState.story();
        if (storyStatData == null) storyStatData = pageState.storyListItem();
        if (storyStatData == null) return -1;
        if (index < 0) return -1;
        if (index >= storyStatData.slidesCount()) return -1;
        return index;
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

    private SlideData getSlideData(StoryReaderPageState pageState) {
        if (pageState.story() == null) return null;
        int index = pageState.slideIndex();
        IReaderContent story = pageState.story();
        StoryData storyData = getStoryData(pageState);
        if (storyData == null) return null;
        return new SlideData(
                storyData,
                pageState.slideIndex(),
                story.slideEventPayload(index)
        );
    }

    private StoryData getStoryData(StoryReaderPageState pageState) {
        IStatData storyStatData = pageState.story();
        if (storyStatData == null) storyStatData = pageState.storyListItem();
        if (storyStatData == null) return null;
        StoryReaderImmutableState readerImmutableState = readerViewModel.readerImmutableState();
        return StoryData.getStoryData(
                storyStatData,
                readerImmutableState.feed(),
                readerImmutableState.sourceType(),
                readerImmutableState.contentType()
        );
    }

    public void openGame(Context context, String gameInstanceId) {
        try {
            StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
            if (pageState.story() == null) return;
            if (core.gamesAPI().gameCanBeOpened(gameInstanceId)) {
                core.screensManager().openScreen(
                        context,
                        new LaunchGameScreenStrategy(core, true)
                                .data(new LaunchGameScreenData(
                                        readerViewModel.readerImmutableState().readerUniqueId(),
                                        getSlideData(pageState),
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

    private void clearPageTimer() {
    }

    @JavascriptInterface
    public void openGame(String gameInstanceId) {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.OPEN_GAME,
                        new ContentId(gameInstanceId)
                )
        );
    }

    @JavascriptInterface
    public void setAudioManagerMode(String mode) { //common
        new AudioManagerUtils(core).setAudioManagerMode(mode);
    }


    @JavascriptInterface
    public void storyShowNext() { //reader
        StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
        clearPageTimer();
        readerViewModel.navigateToIndex(pageState.pageIndex() + 1, ShowStory.ACTION_CUSTOM);
    }

    @JavascriptInterface
    public void storyShowPrev() { //reader
        StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
        clearPageTimer();
        readerViewModel.navigateToIndex(pageState.pageIndex() - 1, ShowStory.ACTION_CUSTOM);
    }

    @JavascriptInterface
    public void storyShowNextSlide(long delay) { //page
        if (delay == 0) {
            StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
            int corIndex = correctIndex(pageState.slideIndex() + 1, pageState);
            if (corIndex >= 0)
                storyReaderPageStateObservable.updateValue(pageState.copy().slideIndex(corIndex));
        }
    }

    @JavascriptInterface
    public void storyShowNextSlide() {
        StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
        int corIndex = correctIndex(pageState.slideIndex() + 1, pageState);
        if (corIndex >= 0)
            storyReaderPageStateObservable.updateValue(pageState.copy().slideIndex(corIndex));
    }

    @JavascriptInterface
    public void storyShowTextInput(String id, String data) { // page/reader?
        manager.storyShowTextInput(id, data);
    }

    @JavascriptInterface
    public void storyStarted() { //page
        manager.storyStartedEvent();
    }

    @JavascriptInterface
    public void storyStarted(double startTime) { //page
        manager.storyStartedEvent();
    }

    @JavascriptInterface
    public void storyLoaded() { //page
        slideLoaded(null);
    }

    @JavascriptInterface
    public void storyLoaded(String data) { //page
        if (data != null) {
            slideLoaded(JsonParser.fromJson(data, StoryLoadedData.class));
        } else {
            slideLoaded(null);
        }
    }

    public void pauseSlide() {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.PAUSE_SLIDE,
                        null
                )
        );
    }

    public void resumeSlide() {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.RESUME_SLIDE,
                        null
                )
        );
    }

    public void startSlide() {
        if (currentSlideIsLoaded) {
            singleTimeEvents.updateValue(
                    new STETypeAndData(STEDataType.START_SLIDE,
                            new StartSlide()
                                    .soundOn(((IASDataSettingsHolder) core.settingsAPI()).isSoundOn())
                    )
            );
        }
    }

    public void restartSlide() {
        if (currentSlideIsLoaded) {
            singleTimeEvents.updateValue(
                    new STETypeAndData(STEDataType.RESTART_SLIDE,
                            new StartSlide()
                                    .soundOn(((IASDataSettingsHolder) core.settingsAPI()).isSoundOn())
                    )
            );
        }
    }

    public void stopSlide() {
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        STEDataType.STOP_SLIDE,
                        null
                )
        );
    }

    private boolean currentSlideIsLoaded = false;

    private void slideLoaded(StoryLoadedData loadedData) {
        StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
        int currentIndex = pageState.slideIndex();
        if (loadedData == null || currentIndex == loadedData.index) {
            currentSlideIsLoaded = true;
            storyReaderPageLoaderStateObservable.updateValue(
                    new StoryReaderPageLoaderState().loaderType(StoryReaderPageLoaderType.HIDDEN)
            );
        } else {
            return;
        }
        if (readerViewModel.getReaderState().currentPage() == pageState.pageIndex()) {
            startSlide();
        } else {
            stopSlide();
        }
    }

    @JavascriptInterface
    public void storyLoadingFailed(String data) { //page
        if (data != null) {
            StoryLoadedData loadedData = JsonParser.fromJson(data, StoryLoadedData.class);
            StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
            if (
                    Objects.equals(pageState.storyId(), Integer.toString(loadedData.id)) &&
                            pageState.slideIndex() == loadedData.index
            ) {
                slideLoadError(pageState.slideIndex());
            }
        }
    }

    private void slideLoadError(int index) {

    }

    @JavascriptInterface
    public void storyStatisticEvent(
            String name,
            String data,
            String eventData,
            boolean forceEnableStatisticV2
    ) {
        if (data != null)
            core.statistic().storiesV2().sendStoryWidgetEvent(
                    name,
                    data,
                    readerViewModel.readerImmutableState().feed(),
                    forceEnableStatisticV2
            );
        if (eventData != null) {
            StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
            if (pageState.story() == null) return;
            final Map<String, String> widgetEventMap = JsonParser.toMap(eventData);
            if (widgetEventMap != null)
                widgetEventMap.put("feed_id", readerViewModel.readerImmutableState().feed());
            core.callbacksAPI().useCallback(IASCallbackType.STORY_WIDGET,
                    new UseIASCallback<StoryWidgetCallback>() {
                        @Override
                        public void use(@NonNull StoryWidgetCallback callback) {
                            callback.widgetEvent(
                                    getSlideData(pageState),
                                    StringsUtils.getNonNull(name),
                                    widgetEventMap
                            );
                        }
                    }
            );
        }
    }

    @JavascriptInterface
    public void share(String id, String data) { //page
        manager.share(id, data);
    }


    @JavascriptInterface
    public void disableVerticalSwipeGesture() {
        readerViewModel.verticalSwipeIsAllowed(false);
    }

    @JavascriptInterface
    public void enableVerticalSwipeGesture() {
        readerViewModel.verticalSwipeIsAllowed(true);
    }

    @JavascriptInterface
    public void disableBackpress() { // reader
        readerViewModel.backPressEnabled(false);
    }

    @JavascriptInterface
    public void enableBackpress() { // reader
        readerViewModel.backPressEnabled(true);
    }

    @JavascriptInterface
    public void storySendData(String data) { //page
        manager.storySendData(data);
    }

    @JavascriptInterface
    public void storySetLocalData(String data, boolean sendToServer) { //page
        StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
        String storyId = pageState.storyId();
        synchronized (localDataLock) {
            core.keyValueStorage().saveString("story" + storyId + "__" +
                    ((IASDataSettingsHolder) core.settingsAPI()).userId(), data);

        }
        if (core.statistic().storiesV1().softDisabled()) return;

        if (sendToServer) {
            core.network().enqueue(
                    core.network().getApi().sendStoryData(
                            storyId,
                            data,
                            ((IASDataSettingsHolder)core.settingsAPI()).sessionIdOrEmpty()
                    ),
                    new NetworkCallback<Response>() {
                        @Override
                        public void onSuccess(Response response) {

                        }

                        @Override
                        public Type getType() {
                            return null;
                        }
                    }
            );
        }
    }

    @JavascriptInterface
    public void closeStory(String reason) { //reader
        manager.closeStory(reason.toLowerCase());
    }

    @JavascriptInterface
    public String storyGetLocalData() {  //page
        StoryReaderPageState pageState = storyReaderPageStateObservable.getValue();
        synchronized (localDataLock) {
            String res = core.keyValueStorage().getString("story" + pageState.storyId()
                    + "__" + ((IASDataSettingsHolder) core.settingsAPI()).userId());
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
