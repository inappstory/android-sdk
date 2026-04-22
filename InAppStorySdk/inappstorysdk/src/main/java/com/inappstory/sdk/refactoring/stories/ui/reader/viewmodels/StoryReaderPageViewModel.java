package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import android.content.Context;
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
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.refactoring.core.downloader.IReaderContentDownloaderSubscriber;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.core.utils.observers.STETypeAndData;
import com.inappstory.sdk.refactoring.core.utils.observers.SingleTimeEvent;
import com.inappstory.sdk.refactoring.core.utils.stedata.ContentId;
import com.inappstory.sdk.refactoring.core.utils.stedata.ContentIdWithIndex;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.JsSendApiRequestResponse;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.StartSlide;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.StoriesSTEDataType;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderButtonsState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderImmutableState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderType;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageTimelineState;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.utils.AudioManagerUtils;
import com.inappstory.sdk.utils.ClipboardUtils;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class StoryReaderPageViewModel implements IReaderContentDownloaderSubscriber {
    private final IASCore core;
    private final StoryReaderViewModel readerViewModel;
    private final StoryReaderPageTimelineManager timelineManager =
            new StoryReaderPageTimelineManager(this);
    private final StoryReaderPageTimerManager timerManager;

    private final Observable<StoryReaderPageLoaderState> storyReaderPageLoaderStateObservable =
            new Observable<>(new StoryReaderPageLoaderState());

    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
            new SingleTimeEvent<>();

    private final Observable<StoryReaderPageState> storyReaderPageStateObservable;

    private final Observable<Boolean> storyReaderPageCloseObservable =
            new Observable<>(true);

    public StoryReaderPageState storyReaderPageState() {
        return storyReaderPageStateObservable.getValue();
    }

    private final Observable<StoryReaderButtonsState> storyReaderPageButtonsStateObservable =
            new Observable<>(new StoryReaderButtonsState());

    public void destroy() {
        core.storyDownloadManager().removeSubscriber(this);
        core.storySlidesDownloadManager().removeSubscriber(this);
    }

    public StoryReaderPageViewModel(
            IASCore core,
            StoryReaderViewModel readerViewModel,
            String storyId,
            int pageIndex
    ) {
        this.core = core;
        this.readerViewModel = readerViewModel;
        timerManager = new StoryReaderPageTimerManager(core, this);

        storyReaderPageStateObservable =
                new Observable<>(
                        new StoryReaderPageState(
                                storyId,
                                pageIndex,
                                readerViewModel.readerImmutableState().contentType()
                        )
                );
        core.storyDownloadManager().addSubscriber(this);
        core.storySlidesDownloadManager().addSubscriber(this);
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

    public void addCloseSubscriber(Observer<Boolean> observer) {
        storyReaderPageCloseObservable.subscribeAndGetValue(observer);
    }

    public void removeCloseSubscriber(Observer<Boolean> observer) {
        storyReaderPageCloseObservable.unsubscribe(observer);
    }

    public void addButtonsStateSubscriber(Observer<StoryReaderButtonsState> observer) {
        storyReaderPageButtonsStateObservable.subscribeAndGetValue(observer);
    }

    public void removeButtonsStateSubscriber(Observer<StoryReaderButtonsState> observer) {
        storyReaderPageButtonsStateObservable.unsubscribe(observer);
    }

    public void addTimelineStateSubscriber(Observer<StoryReaderPageTimelineState> observer) {
        timelineManager.addTimelineStateSubscriber(observer);
    }

    public void removeTimelineStateSubscriber(Observer<StoryReaderPageTimelineState> observer) {
        timelineManager.removeTimelineStateSubscriber(observer);
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

    public void nextSlideAuto() {

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
            StoryReaderPageState pageState = storyReaderPageState();
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


    private void pauseTimers() {
        timerManager.pauseSlideTimer();
        timelineManager.stopTimer();
    }


    private void clearTimer() {
        timelineManager.clearTimer();
    }

    public void updateTimeline(final UpdateTimelineData data) {
        StoryReaderPageState pageState = storyReaderPageState();
        if (pageState.slideIndex() != data.slideIndex) return;
        if (data.showError) {
            slideLoadError(data.slideIndex);
            pauseTimers();
            clearTimer();
        } else if (data.showLoader) {
            storyReaderPageLoaderStateObservable.updateValue(
                    new StoryReaderPageLoaderState()
                            .loaderType(StoryReaderPageLoaderType.LOADING)
            );
        } else {
            storyReaderPageLoaderStateObservable.updateValue(
                    new StoryReaderPageLoaderState()
                            .loaderType(StoryReaderPageLoaderType.HIDDEN)
            );
        }
        if (data.action == null) return;
        if (data.action.equals("start")) {
            timerManager.startSlideTimer(data.duration, data.currentTime);
            timelineManager.startTimer(data.currentTime, data.slideIndex, data.duration);
        } else if (data.action.equals("pause")) {
            pauseTimers();
        } else if (data.action.equals("stop")) {
            pauseTimers();
        } else if (data.action.equals("before_start")) {
            timelineManager.setCurrentIndex(data.slideIndex);
        }
    }


    private void clearPageTimer() {
        timerManager.setTimerDuration(0);
        timelineManager.stopTimer();
    }


    public void pauseSlide() {
        singleTimeEvents.updateValue(
                new STETypeAndData(StoriesSTEDataType.PAUSE_SLIDE,
                        null
                )
        );
    }

    public void resumeSlide() {
        singleTimeEvents.updateValue(
                new STETypeAndData(StoriesSTEDataType.RESUME_SLIDE,
                        null
                )
        );
    }

    public void startSlide() {
        if (currentSlideIsLoaded) {
            singleTimeEvents.updateValue(
                    new STETypeAndData(StoriesSTEDataType.START_SLIDE,
                            new StartSlide()
                                    .soundOn(((IASDataSettingsHolder) core.settingsAPI()).isSoundOn())
                    )
            );
        }
    }

    public void restartSlide() {
        if (currentSlideIsLoaded) {
            singleTimeEvents.updateValue(
                    new STETypeAndData(StoriesSTEDataType.RESTART_SLIDE,
                            new StartSlide()
                                    .soundOn(((IASDataSettingsHolder) core.settingsAPI()).isSoundOn())
                    )
            );
        }
    }

    public void stopSlide() {
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        StoriesSTEDataType.STOP_SLIDE,
                        null
                )
        );
    }

    private boolean currentSlideIsLoaded = false;

    private void slideLoaded(StoryLoadedData loadedData) {
        StoryReaderPageState pageState = storyReaderPageState();
        int currentIndex = pageState.slideIndex();
        if (loadedData == null || currentIndex == loadedData.index) {
            currentSlideIsLoaded = true;
            storyReaderPageLoaderStateObservable.updateValue(
                    new StoryReaderPageLoaderState().loaderType(StoryReaderPageLoaderType.HIDDEN)
            );
        } else {
            return;
        }
        if (readerViewModel.readerState().currentPage() == pageState.pageIndex()) {
            startSlide();
        } else {
            stopSlide();
        }
    }


    @Override
    public ContentIdAndType contentIdAndType() {
        StoryReaderPageState state = storyReaderPageState();
        return new ContentIdAndType(
                Integer.parseInt(state.storyId()),
                state.contentType()
        );
    }

    @Override
    public void contentLoadError() {
        storyReaderPageLoaderStateObservable.updateValue(
                new StoryReaderPageLoaderState().loaderType(StoryReaderPageLoaderType.REFRESH)
        );
    }

    @Override
    public void slideLoadError(int index) {
        storyReaderPageLoaderStateObservable.updateValue(
                new StoryReaderPageLoaderState().loaderType(StoryReaderPageLoaderType.REFRESH)
        );
        timelineManager.setCurrentIndex(index);
    }

    @Override
    public void contentLoadSuccess(IReaderContent content) {
        storyReaderPageStateObservable.updateValue(storyReaderPageState().copy().story((StoryDTO) content));
        core.storySlidesDownloadManager().addTasks(content, contentIdAndType().contentType);
    }

    @Override
    public void slideLoadSuccess(int index) {
        StoryReaderPageState pageState = storyReaderPageState();
        if (pageState.slideIndex() != index) return;
    }

    private void sendStoryDataToServer(String storyId, String data) {
        core.network().enqueue(
                core.network().getApi().sendStoryData(
                        storyId,
                        data,
                        readerViewModel.readerImmutableState().sessionParameters().sessionId()
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
            updateTimeline(updateTimelineData);
        }
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
                        StoriesSTEDataType.FREEZE_UI,
                        null
                )
        );
    }


    @JavascriptInterface
    public void storyRenderReady() {
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        StoriesSTEDataType.RENDER_READY,
                        null
                )
        );
    }


    @JavascriptInterface
    public void storyUnfreezeUI() { //reader
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        StoriesSTEDataType.UNFREEZE_UI,
                        null
                )
        );
    }

    @JavascriptInterface
    public void storyShowSlide(int index) { //page
        StoryReaderPageState pageState = storyReaderPageState();
        if (pageState.slideIndex() != index) {
            int corIndex = correctIndex(index, pageState);
            if (corIndex >= 0)
                changeSlide(index);
        }
    }

    private void changeSlide(int index) {
        StoryReaderPageState pageState = storyReaderPageState();
        IStatData storyStatData = pageState.story();
        if (storyStatData == null) storyStatData = pageState.storyListItem();
        storyReaderPageStateObservable.updateValue(pageState.copy().slideIndex(index));
        ContentIdAndType contentIdAndType = contentIdAndType();
        core.storySlidesDownloadManager().renewStoryPriorities(
                contentIdAndType,
                index,
                storyStatData.slidesCount()
        );
        if (core.storySlidesDownloadManager().slideIsLoaded(contentIdAndType, index))
            slideLoadSuccess(index);
    }

    @JavascriptInterface
    public void showSingleStory(int id, int index) {

        StoryReaderPageState pageState = storyReaderPageState();
        if (!Objects.equals(pageState.storyId(), Integer.toString(id))) {
            singleTimeEvents.updateValue(
                    new STETypeAndData(StoriesSTEDataType.OPEN_STORY,
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
                        new STETypeAndData(StoriesSTEDataType.JS_SEND_API_RESPONSE,
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
        singleTimeEvents.updateValue(
                new STETypeAndData(StoriesSTEDataType.OPEN_GAME,
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
        StoryReaderPageState pageState = storyReaderPageState();
        clearPageTimer();
        readerViewModel.navigateToIndex(pageState.pageIndex() + 1, ShowStory.ACTION_CUSTOM);
    }

    @JavascriptInterface
    public void storyShowPrev() { //reader
        StoryReaderPageState pageState = storyReaderPageState();
        clearPageTimer();
        readerViewModel.navigateToIndex(pageState.pageIndex() - 1, ShowStory.ACTION_CUSTOM);
    }

    @JavascriptInterface
    public void storyShowNextSlide(long delay) { //page
        if (delay == 0) {
            StoryReaderPageState pageState = storyReaderPageState();
            int corIndex = correctIndex(pageState.slideIndex() + 1, pageState);
            if (corIndex >= 0)
                storyReaderPageStateObservable.updateValue(pageState.copy().slideIndex(corIndex));
        }
    }

    @JavascriptInterface
    public void storyShowNextSlide() {
        StoryReaderPageState pageState = storyReaderPageState();
        int corIndex = correctIndex(pageState.slideIndex() + 1, pageState);
        if (corIndex >= 0)
            storyReaderPageStateObservable.updateValue(pageState.copy().slideIndex(corIndex));
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

    @JavascriptInterface
    public void storyLoadingFailed(String data) { //page
        if (data != null) {
            StoryLoadedData loadedData = JsonParser.fromJson(data, StoryLoadedData.class);
            StoryReaderPageState pageState = storyReaderPageState();
            if (
                    Objects.equals(pageState.storyId(), Integer.toString(loadedData.id)) &&
                            pageState.slideIndex() == loadedData.index
            ) {
                slideLoadError(pageState.slideIndex());
            }
        }
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
            StoryReaderPageState pageState = storyReaderPageState();
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
    public void storySetLocalData(String data, boolean sendToServer) { //page
        StoryReaderPageState pageState = storyReaderPageState();
        String storyId = pageState.storyId();
        synchronized (localDataLock) {
            core.keyValueStorage().saveString("story" + storyId + "__" +
                    ((IASDataSettingsHolder) core.settingsAPI()).userId(), data);

        }
        if (core.statistic().storiesV1().softDisabled()) return;

        if (sendToServer) {
            sendStoryDataToServer(storyId, data);
        }
    }

    @JavascriptInterface
    public String storyGetLocalData() {  //page
        StoryReaderPageState pageState = storyReaderPageState();
        synchronized (localDataLock) {
            String res = core.keyValueStorage().getString("story" + pageState.storyId()
                    + "__" + ((IASDataSettingsHolder) core.settingsAPI()).userId());
            return res == null ? "" : res;
        }
    }

    private final Object localDataLock = new Object();

    @JavascriptInterface
    public void shareSlideScreenshotCb(String shareId, boolean result) {  //page
        //TODO("Not implemented")
    }

    @JavascriptInterface
    public void productCartUpdate(String productCartData, String callbacks) { //page?
        //TODO("Not implemented")
    }

    @JavascriptInterface
    public void productCartClicked() {
        //TODO("Not implemented")
    } //page?

    @JavascriptInterface
    public void productCartGetState(String callbacks) {
        //TODO("Not implemented")
    }

    @JavascriptInterface
    public void share(String id, String data) { //page
        //TODO("Not implemented")
    }

    @JavascriptInterface
    public void storyShowTextInput(String id, String data) { // page/reader?
        //TODO("Not implemented")
    }


    @JavascriptInterface
    public void closeStory(String reason) {
        String reasonLow = reason.toLowerCase();
        int closeStoryAction = CloseStory.CLICK;
        switch (reasonLow) {
            case "custom":
                closeStoryAction = CloseStory.CUSTOM;
                break;
            case "swipe":
                closeStoryAction = CloseStory.SWIPE;
                break;
            case "auto":
                closeStoryAction = CloseStory.AUTO;
                break;
        }
        core.screensManager().getStoryScreenHolder().closeScreenWithAction(closeStoryAction);
    }

    @JavascriptInterface
    public void storySendData(String data) {
        if (core.statistic().storiesV1().softDisabled()) return;
        StoryReaderPageState pageState = storyReaderPageState();
        String storyId = pageState.storyId();
        sendStoryDataToServer(storyId, data);
    }

    @JavascriptInterface
    public void storyStarted() { //page
        StoryReaderPageState pageState = storyReaderPageState();
        if (pageState.story() == null) return;
        readerViewModel.swipeUpIsAllowed(pageState.story().hasSwipeUp());
        readerViewModel.closeIsAllowed(!pageState.story().disableClose());
        core.statistic().profiling().setReady(pageState.storyId() + "_" + pageState.slideIndex());
    }

    @JavascriptInterface
    public void storyStarted(double startTime) { //page
        StoryReaderPageState pageState = storyReaderPageState();
        if (pageState.story() == null) return;
        readerViewModel.swipeUpIsAllowed(pageState.story().hasSwipeUp());
        readerViewModel.closeIsAllowed(!pageState.story().disableClose());
        core.statistic().profiling().setReady(pageState.storyId() + "_" + pageState.slideIndex());
    }
}
