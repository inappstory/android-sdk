package com.inappstory.sdk.core.banners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.banners.BannerWidgetCallback;
import com.inappstory.sdk.banners.ShowBannerCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.domain.stedata.AutoSlideEndData;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.inappmessage.domain.stedata.JsSendApiRequestData;
import com.inappstory.sdk.inappmessage.domain.stedata.STEDataType;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.api.models.ContentId;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.SlideLinkObject;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.StoriesViewManager;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;
import com.inappstory.sdk.stories.utils.WebPageConvertCallback;
import com.inappstory.sdk.stories.utils.WebPageConverter;
import com.inappstory.sdk.utils.ScheduledTPEManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BannerViewModel implements IBannerViewModel {

    private final int bannerId;
    private final String bannerPlace;
    private final Observable<BannerState> stateObservable =
            new Observable<>(new BannerState());

    private final IASCore core;

    private ScheduledFuture scheduledFuture;
    private final ScheduledTPEManager executorService = new ScheduledTPEManager();

    public void iterationId(String iterationId) {
        this.iterationId = iterationId;
    }

    @Override
    public void updateTimeline(String strData) {
        if (strData != null && !strData.isEmpty()) {
            UpdateTimelineData data = JsonParser.fromJson(strData, UpdateTimelineData.class);
            if (data.showError) {
                slideLoadError(0);
                updateCurrentLoadState(BannerLoadStates.FAILED);
                cancelTask();
            } else if (data.showLoader) {
                Log.e("updateTimeline", "showLoader");
                updateCurrentLoadState(BannerLoadStates.LOADING);
                cancelTask();
            } else {
                updateCurrentLoadState(BannerLoadStates.LOADED);
            }
            if (data.action == null) return;
            if (data.action.equals("start")) {
                startTimer(data.duration, data.duration - data.currentTime);
            } else if (data.action.equals("pause")) {
                pauseSlide();
            } else if (data.action.equals("stop")) {
                stopSlide();
            }
        }
    }

    private String iterationId = UUID.randomUUID().toString();

    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
            new SingleTimeEvent<>();

    private final IBannerPlaceViewModel bannerPlaceViewModel;

    public BannerViewModel(int bannerId, String bannerPlace, IASCore core, IBannerPlaceViewModel bannerPlaceViewModel) {
        this.bannerId = bannerId;
        this.bannerPlace = bannerPlace;
        this.core = core;
        stateObservable.setValue(
                new BannerState()
                        .bannerId(bannerId)
                        .bannerPlace(bannerPlace)
                        .loadState(BannerLoadStates.EMPTY)
        );
        this.bannerPlaceViewModel = bannerPlaceViewModel;
    }


    @Override
    public BannerState getCurrentBannerState() {
        return stateObservable.getValue();
    }

    @Override
    public BannerData getCurrentBannerData() {
        return new BannerData(
                bannerId,
                bannerPlace,
                SourceType.BANNER_PLACE
        );
    }

    @Override
    public ContentIdAndType contentIdAndType() {
        return new ContentIdAndType(
                bannerId,
                ContentType.BANNER
        );
    }

    private void loadFailed() {
        updateCurrentLoadState(BannerLoadStates.FAILED);
        stateObservable.updateValue(
                getCurrentBannerState().copy().contentStatus(-1).content(null)
        );
    }

    @Override
    public void contentLoadError() {
    }

    @Override
    public void slideLoadError(int index) {
        loadFailed();
    }

    @Override
    public void contentLoadSuccess(IReaderContent content) {
    }

    @Override
    public void slideLoadSuccess(int index) {
        BannerDownloadManager downloadManager = core.contentLoader().bannerDownloadManager();
        downloadManager.removeSubscriber(this);
        final BannerState readerState = getCurrentBannerState();
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(
                        bannerId,
                        ContentType.BANNER
                );
        if (readerContent == null) return;
        String slideContent = readerContent.slideByIndex(0);
        if (slideContent == null) return;
        WebPageConvertCallback callback = new WebPageConvertCallback() {
            @Override
            public void onConvert(String replaceData, String firstData, int lastIndex) {
                stateObservable.updateValue(
                        readerState
                                .copy()
                                .loadState(BannerLoadStates.LOADED)
                                .contentStatus(1)
                                .content(firstData)
                );
            }
        };
        WebPageConverter converter = new WebPageConverter();
        converter.replaceDataAndLoad(slideContent, readerContent, index, callback);
    }

    @Override
    public Integer externalSubscriber() {
        return null;
    }

    public boolean loadContent() {
        final BannerState state = getCurrentBannerState();
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(
                        bannerId,
                        ContentType.BANNER
                );
        BannerDownloadManager downloadManager = core.contentLoader().bannerDownloadManager();
        downloadManager.addSubscriber(this);
        if (readerContent != null && downloadManager.allSlidesLoaded(readerContent)) {
            //    updateCurrentLoadState(BannerLoadStates.LOADED);
            slideLoadSuccess(0);
        } else {
            if (state.loadState() != BannerLoadStates.LOADING) {
                updateCurrentLoadState(BannerLoadStates.LOADING);
            }
            downloadManager.addBannerTask(bannerId, null);
        }
        return true;
    }

    @Override
    public void addSubscriber(Observer<BannerState> observable) {
        this.stateObservable.subscribeAndGetValue(observable);
    }

    @Override
    public void removeSubscriber(Observer<BannerState> observable) {
        this.stateObservable.unsubscribe(observable);
    }

    @Override
    public void updateCurrentLoadState(BannerLoadStates bannerLoadState) {
        this.stateObservable.updateValue(
                this.stateObservable.getValue()
                        .copy()
                        .loadState(bannerLoadState)
        );
    }

    @Override
    public void pauseSlide() {
        synchronized (timerLock) {
            paused = true;
            pauseShiftStart = System.currentTimeMillis();
        }
    }

    @Override
    public void resumeSlide() {
        synchronized (timerLock) {
            if (pauseShiftStart != 0) {
                pauseShift += System.currentTimeMillis() - pauseShiftStart;
                paused = false;
            }
        }
    }

    @Override
    public void stopSlide() {
        stopTimer();
    }

    private final Object timerLock = new Object();

    private void stopTimer() {
        synchronized (timerLock) {
            if (lastStartTimer == -1) return;
        }
        cancelTask();
        synchronized (timerLock) {
            pauseShift = 0;
            paused = false;
            lastStartTimer = -1;
        }
    }

    @Override
    public void slideClick(String payload) {
        if (payload != null && !payload.isEmpty()) {
            SlideLinkObject object = JsonParser.fromJson(payload, SlideLinkObject.class);
            if (object != null) {
                ClickAction action = ClickAction.BUTTON;
                if (object.getLink().getType().equals("url")) {
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        if ("swipeUpLink".equals(object.getType())) {
                            action = ClickAction.SWIPE;
                        }
                    }
                    singleTimeEvents.updateValue(
                            new STETypeAndData(
                                    STEDataType.CALL_TO_ACTION,
                                    new CallToActionData()
                                            .contentData(
                                                    getCurrentBannerData()
                                            )
                                            .link(
                                                    object.getLink().getTarget()
                                            )
                                            .clickAction(action)
                            )
                    );
                }
            }
        }
    }

    @Override
    public void slideLoadingFailed(String data) {
        loadFailed();
    }

    @Override
    public void showSingleStory(int id, int index) {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.OPEN_STORY,
                        new ContentIdWithIndex(id, index)
                )
        );
    }

    @Override
    public void sendApiRequest(String data) {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.JS_SEND_API_REQUEST,
                        new JsSendApiRequestData()
                                .data(data)
                )
        );
    }

    @Override
    public void vibrate(int[] vibratePattern) {

    }

    @Override
    public void openGame(String gameInstanceId) {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.OPEN_GAME,
                        new ContentId(gameInstanceId)
                )
        );
    }

    @Override
    public void setAudioManagerMode(String mode) {

    }

    @Override
    public void slideStarted(Double startTime) {

    }

    @Override
    public void slideLoaded(String data) {
        //updateCurrentLoadState(BannerLoadStates.LOADED);
        stateObservable.updateValue(
                stateObservable
                        .getValue()
                        .copy()
                        .slideJSStatus(1)
        );
    }

    @Override
    public void statisticEvent(final String name, String data, String eventData) {
        long shift = 0;
        long lastTimer = 0;
        synchronized (timerLock) {
            if (lastStartTimer == -1) return;
            lastTimer = lastStartTimer;
            shift = pauseShift;
        }
        if (data != null) {
            core.statistic().bannersV1().sendWidgetEvent(
                    name,
                    data,
                    bannerId,
                    0,
                    1,
                    System.currentTimeMillis() - shift - lastTimer,
                    iterationId
            );
            // TODO Add duration first
        }
        if (eventData != null) {
            final Map<String, String> widgetEventMap = JsonParser.toMap(eventData);
            core.callbacksAPI().useCallback(IASCallbackType.BANNER_WIDGET,
                    new UseIASCallback<BannerWidgetCallback>() {
                        @Override
                        public void use(@NonNull BannerWidgetCallback callback) {
                            callback.bannerWidget(
                                    getCurrentBannerData(),
                                    StringsUtils.getNonNull(name),
                                    widgetEventMap
                            );
                        }
                    }
            );
        }
    }

    @Override
    public void share(String id, String data) {

    }

    @Override
    public void freezeUI() {

    }

    @Override
    public void unfreezeUI() {

    }

    private final Object localDataLock = new Object();

    @Override
    public String getLocalUserData() {
        synchronized (localDataLock) {
            String res = core.keyValueStorage().getString("banner" +
                    getCurrentBannerState().bannerId()
                    + "__" + ((IASDataSettingsHolder) core.settingsAPI()).userId());
            return res == null ? "" : res;
        }
    }

    @Override
    public void sendData(String data) {
        BannerState bannerState = getCurrentBannerState();
        if (bannerState == null) return;
        if (core.statistic().iamV1().disabled()) return;
        core.network().enqueue(
                core.network().getApi().sendBannerUserData(
                        Integer.toString(bannerState.bannerId()),
                        data
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

    @Override
    public void showNext() {
        if (bannerPlaceViewModel != null) bannerPlaceViewModel.showNext();
    }

    @Override
    public void setLocalUserData(String data, boolean sendToServer) {
        BannerState bannerState = getCurrentBannerState();
        if (bannerState == null) return;
        synchronized (localDataLock) {
            core.keyValueStorage().saveString("banner" +
                    bannerState.bannerId() + "__" +
                    ((IASDataSettingsHolder) core.settingsAPI()).userId(), data);
        }
        if (core.statistic().iamV1().disabled()) return;
        if (sendToServer) {
            core.network().enqueue(
                    core.network().getApi().sendBannerUserData(
                            Integer.toString(bannerState.bannerId()),
                            data
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

    @Override
    public void shareSlideScreenshotCb(String shareId, boolean result) {

    }


    private long lastStartTimer = -1;
    private long pauseShift = 0;
    private long pauseShiftStart = 0;
    private boolean paused;
    private long timerDuration = 0L;

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            boolean cancel = false;
            synchronized (timerLock) {
                if (paused) return;
                cancel = timerDuration > 0 && System.currentTimeMillis() - lastStartTimer >= timerDuration;
            }
            if (cancel) {
                cancelTask();
                synchronized (timerLock) {
                    pauseShift = 0;
                    paused = false;
                    lastStartTimer = -1;
                }
                autoSlideEnd();
            }

        }
    };

    private void autoSlideEnd() {
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        STEDataType.AUTO_SLIDE_END,
                        new AutoSlideEndData()
                )
        );
    }

    private void cancelTask() {
        if (scheduledFuture != null)
            scheduledFuture.cancel(false);
        scheduledFuture = null;
        executorService.shutdown();
    }

    private void startTimer(long maxTimerDuration, long timerDuration) {
        if (maxTimerDuration == 0) return;
        synchronized (timerLock) {
            lastStartTimer = System.currentTimeMillis();
            pauseShift = 0;
            this.timerDuration = timerDuration;
        }
        scheduledFuture = executorService.scheduleAtFixedRate(
                timerTask,
                1L,
                50L,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void bannerIsShown() {
        core.callbacksAPI().useCallback(
                IASCallbackType.SHOW_BANNER,
                new UseIASCallback<ShowBannerCallback>() {
                    @Override
                    public void use(@NonNull ShowBannerCallback callback) {
                        callback.showBanner(
                                getCurrentBannerData()
                        );
                    }
                }
        );
        core.statistic().bannersV1().sendOpenEvent(bannerId, 0, 1, iterationId);
    }

    private boolean bannerIsActive;

    @Override
    public boolean bannerIsActive() {
        return bannerIsActive;
    }

    @Override
    public void bannerIsActive(boolean active) {
        this.bannerIsActive = active;
    }
}
