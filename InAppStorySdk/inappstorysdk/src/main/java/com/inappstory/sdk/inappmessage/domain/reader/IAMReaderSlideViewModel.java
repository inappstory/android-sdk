package com.inappstory.sdk.inappmessage.domain.reader;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.inappmessages.InAppMessageDownloadManager;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.inappmessage.InAppMessageSlideData;
import com.inappstory.sdk.inappmessage.InAppMessageWidgetCallback;
import com.inappstory.sdk.inappmessage.ShowInAppMessageSlideCallback;
import com.inappstory.sdk.inappmessage.domain.stedata.JsSendApiRequestData;
import com.inappstory.sdk.inappmessage.domain.stedata.STEDataType;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.inappmessage.domain.stedata.SlideInCacheData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.ContentId;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.SlideLinkObject;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;
import com.inappstory.sdk.stories.utils.WebPageConverter;
import com.inappstory.sdk.utils.ClipboardUtils;
import com.inappstory.sdk.utils.StringsUtils;
import com.inappstory.sdk.utils.UrlEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IAMReaderSlideViewModel implements IIAMReaderSlideViewModel {
    private final Observable<IAMReaderSlideState> slideStateObservable =
            new Observable<>(new IAMReaderSlideState());
    private final IAMReaderSlideStatState slideTimeState = new IAMReaderSlideStatState();

    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
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
    public void readerIsOpened(boolean fromScratch) {
        IAMReaderSlideState slideState = slideStateObservable.getValue();
        Integer iamId = readerViewModel.getCurrentState().iamId;
        if (iamId == null) return;
        if (fromScratch)
            slideTimeState.create(
                    UUID.randomUUID().toString()
            );
        else
            slideTimeState.resume();
        core.statistic().iamV1().sendOpenEvent(
                iamId,
                slideState.slideIndex(),
                slideState.slidesTotal(),
                slideTimeState.iterationId(),
                fromScratch
        );
    }

    @Override
    public void readerIsClosing() {
        IAMReaderSlideState slideState = slideStateObservable.getValue();
        Integer iamId = readerViewModel.getCurrentState().iamId;
        if (iamId == null) return;
        int[] slideTimes = slideTimeState.totalSlideTimes();
        List<IAMReaderSlideStatObject> slideStatObjects = new ArrayList<>();
        String slideAnalytics = null;
        if (slideTimes != null) {
            for (int i = 0; i < slideTimes.length; i++) {
                if (slideTimes[i] > 0) {
                    slideStatObjects.add(new IAMReaderSlideStatObject(i, slideTimes[i]));
                }
            }
            if (!slideStatObjects.isEmpty()) {
                try {
                    slideAnalytics = JsonParser.getJson(slideStatObjects);
                    //   Log.e("SlideTimes", slideAnalytics);
                    //  slideAnalytics = new UrlEncoder().encode(slideAnalytics);
                    //  Log.e("SlideTimes", slideAnalytics);
                } catch (Exception ignored) {

                }
            }
        }
        core.statistic().iamV1().sendCloseEvent(
                iamId,
                slideState.slideIndex(),
                slideState.slidesTotal(),
                slideTimeState.totalTime(),
                slideAnalytics,
                slideTimeState.iterationId()
        );
    }


    @Override
    public void closeReader() {
        core.screensManager().getIAMScreenHolder().closeScreen();
    }

    @Override
    public void updateLayout() {
        IAMReaderState readerState = readerViewModel.getCurrentState();
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(
                        readerState.iamId,
                        ContentType.IN_APP_MESSAGE
                );
        if (readerContent == null) return;
        WebPageConverter converter = new WebPageConverter();
        String layout = converter.replaceLayout(readerContent);
        slideTimeState.updateSlidesCount(readerContent.actualSlidesCount());
        slideStateObservable.updateValue(
                slideStateObservable
                        .getValue()
                        .copy()
                        .slidesTotal(readerContent.actualSlidesCount())
                        .contentStatus(1)
                        .layout(layout)
        );
    }

    @Override
    public void onCardLoadingStateChange(int state, String reason) {
        switch (state) {
            case 0:
                readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.LOADING);
                break;
            case 1:
                try {
                    handler.removeCallbacks(contentFailedByTimeout);
                } catch (Exception e) {
                }
                readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.LOADED);
                break;
            case 2:
                try {
                    handler.removeCallbacks(contentFailedByTimeout);
                } catch (Exception e) {
                }
                readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.FAILED);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEvent(String name, String event) {
        if (name == null || name.isEmpty()) return;
        Log.e("OnEvent", name + " " + event);
        switch (name) {
            case "showSlide":
                showSlideEvent(event);
                break;
            case "slideLeft":
                slideLeftEvent(event);
                break;
        }
    }

    private void slideLeftEvent(String event) {
        if (event == null || event.isEmpty()) return;
        final SlideLeftJSPayload slideLeftJSPayload = JsonParser.fromJson(
                event,
                SlideLeftJSPayload.class
        );
        if (slideLeftJSPayload != null) {
            slideTimeState.leftSlide(slideLeftJSPayload.index);
        }
    }

    private void showSlideEvent(String event) {
        if (event == null || event.isEmpty()) return;
        final ShowSlideJSPayload showSlideJSPayload = JsonParser.fromJson(
                event,
                ShowSlideJSPayload.class
        );
        if (showSlideJSPayload != null) {
            slideStateObservable.updateValue(
                    slideStateObservable
                            .getValue()
                            .copy()
                            .slideIndex(showSlideJSPayload.index)
            );
            slideTimeState.updateSlideIndex(showSlideJSPayload.index);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    final InAppMessageSlideData slideData = slideData();
                    if (slideData != null) {
                        core.callbacksAPI().useCallback(
                                IASCallbackType.SHOW_IN_APP_MESSAGE_SLIDE,
                                new UseIASCallback<ShowInAppMessageSlideCallback>() {
                                    @Override
                                    public void use(@NonNull ShowInAppMessageSlideCallback callback) {
                                        callback.showSlide(
                                                slideData
                                        );
                                    }
                                }
                        );
                    }
                }
            });
        }
    }

    @Override
    public InAppMessageSlideData slideData() {
        int index = slideStateObservable
                .getValue().slideIndex();
        IAMReaderState state = readerViewModel.getCurrentState();
        Integer iamId = state.iamId;
        if (iamId != null) {
            IInAppMessage readerContent =
                    (IInAppMessage) core.contentHolder().readerContent().getByIdAndType(
                            state.iamId,
                            ContentType.IN_APP_MESSAGE
                    );
            if (readerContent != null) {
                return new InAppMessageSlideData(
                        index,
                        readerContent.slideEventPayload(
                                index
                        ),
                        readerViewModel.getCurrentInAppMessageData()
                );
            }
        }
        return null;
    }

    @Override
    public ContentIdWithIndex iamId() {
        Integer iamId = readerViewModel.getCurrentState().iamId;
        if (iamId != null) {
            return new ContentIdWithIndex(iamId, 0);
        }
        return null;
    }

    @Override
    public String modifyContent(String content) {
        return content.replace("<head>",
                "<head>" + "<style> html { background: #00000000 !important; } </style>");
    }

    public void slideClick(String payload) {
        if (payload != null && !payload.isEmpty()) {

            Log.e("IASClickPayload", payload);
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
                                                    readerViewModel.
                                                            getCurrentInAppMessageData()
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
    public void resumeSlideTimer() {
        slideTimeState.resume();
    }

    @Override
    public void clear() {
        slideTimeState.clear();
        slideStateObservable.updateValue(new IAMReaderSlideState());
    }


    public void updateTimeline(String data) {
    }

    public void storyLoadingFailed(String data) {
        try {
            handler.removeCallbacks(contentFailedByTimeout);
        } catch (Exception e) {
        }
        readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.CONTENT_FAILED_CLOSE);
    }

    public void storyShowSlide(int index) {

    }

    public void showSingleStory(int id, int index) {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.OPEN_STORY,
                        new ContentIdWithIndex(id, index)
                )
        );
    }

    public void sendApiRequest(String data) {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.JS_SEND_API_REQUEST,
                        new JsSendApiRequestData()
                                .data(data)
                )
        );
    }

    public void vibrate(int[] vibratePattern) {

    }

    public void openGame(String gameInstanceId) {
        singleTimeEvents.updateValue(
                new STETypeAndData(STEDataType.OPEN_GAME,
                        new ContentId(gameInstanceId)
                )
        );
    }

    public void setAudioManagerMode(String mode) {

    }

    public void storyShowNext() {

    }

    public void storyShowPrev() {

    }

    @Override
    public void writeToClipboard(String payload) {
        ClipboardUtils.writeToClipboard(payload, core.appContext());
    }

    public void storyShowNextSlide(long delay) {
    }

    public void storyShowNextSlide() {

    }

    public void storyShowTextInput(String id, String data) {

    }

    public void storyStarted() {
    }

    public void storyStarted(double startTime) {

    }

    public void storyLoaded() {
        readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.CONTENT_LOADED);
        try {

            Log.e("RemoveTimeoutCallback", "storyLoaded");
            handler.removeCallbacks(contentFailedByTimeout);
        } catch (Exception e) {
        }
        slideStateObservable.updateValue(
                slideStateObservable
                        .getValue()
                        .copy()
                        .slideJSStatus(1)
        );
    }

    public void storyLoaded(String data) {
        readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.CONTENT_LOADED);
        try {
            Log.e("RemoveTimeoutCallback", "storyLoaded");
            handler.removeCallbacks(contentFailedByTimeout);
        } catch (Exception e) {
        }
        slideStateObservable.updateValue(
                slideStateObservable
                        .getValue()
                        .copy()
                        .slideJSStatus(1)
        );
    }

    public void statisticEvent(
            final String name,
            final String data,
            final String eventData
    ) {
        Integer iamId = readerViewModel.getCurrentState().iamId;
        IAMReaderSlideState slideState = slideStateObservable.getValue();
        if (data != null) {
            core.statistic().iamV1().sendWidgetEvent(
                    name,
                    data,
                    iamId,
                    slideState.slideIndex(),
                    slideState.slidesTotal(),
                    slideTimeState.totalTime(),
                    slideTimeState.iterationId()
            );
        }
        if (eventData != null) {
            final Map<String, String> widgetEventMap = JsonParser.toMap(eventData);
            String event = readerViewModel.getCurrentState().event;
            if (widgetEventMap != null)
                widgetEventMap.put("event", event);
            core.callbacksAPI().useCallback(IASCallbackType.IN_APP_MESSAGE_WIDGET,
                    new UseIASCallback<InAppMessageWidgetCallback>() {
                        @Override
                        public void use(@NonNull InAppMessageWidgetCallback callback) {
                            callback.inAppMessageWidget(
                                    readerViewModel.getCurrentInAppMessageData(),
                                    StringsUtils.getNonNull(name),
                                    widgetEventMap
                            );
                        }
                    }
            );
        }
    }

    public void emptyLoaded() {

    }

    public void share(String id, String data) {

    }

    public void storyFreezeUI() {
    }

    public void storySendData(String data) {
        IAMReaderState readerState = readerViewModel.getCurrentState();
        if (readerState == null) return;
        if (core.statistic().iamV1().softDisabled()) return;
        core.network().enqueue(
                core.network().getApi().sendIAMUserData(
                        Integer.toString(readerState.iamId),
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

    public void setLocalUserData(String data, boolean sendToServer) {
        IAMReaderState readerState = readerViewModel.getCurrentState();
        if (readerState == null) return;
        IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        CachedSessionData sessionData = settingsHolder.sessionData();
        if (sessionData == null) return;
        synchronized (localDataLock) {
            core.keyValueStorage().saveString("iam" +
                    readerState.iamId + "__" +
                    sessionData.userId, data);
        }
        if (core.statistic().iamV1().softDisabled()) return;
        if (sendToServer) {
            core.network().enqueue(
                    core.network().getApi().sendIAMUserData(
                            Integer.toString(readerState.iamId),
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

    private final Object localDataLock = new Object();

    public String getLocalUserData() {
        IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        CachedSessionData sessionData = settingsHolder.sessionData();
        if (sessionData == null) return "";
        synchronized (localDataLock) {
            String res = core.keyValueStorage().getString("iam" +
                    readerViewModel.getCurrentState().iamId
                    + "__" + sessionData.userId);
            return res == null ? "" : res;
        }
    }

    public void shareSlideScreenshotCb(String shareId, boolean result) {
    }

    public void defaultTap(String val) {
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

    private final Object steLock = new Object();

    private final List<STETypeAndData> queuedSTEs = new ArrayList<>();


    @Override
    public void slideLoadError(int index) {
        /*if (slideStateObservable.getValue().slideIndex() == index) {
            readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.CONTENT_FAILED);
            readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.FAILED);
            return;
        }*/
        STETypeAndData steTypeAndData = new STETypeAndData(
                STEDataType.SLIDE_IN_CACHE,
                new SlideInCacheData()
                        .index(index).status(0)
        );
        if (slideStateObservable.getValue().renderReady()) {
            singleTimeEvents.updateValue(
                    steTypeAndData
            );
        }
    }

    @Override
    public void contentLoadSuccess(IReaderContent content) {

    }

    @Override
    public void slideLoadSuccess(int index) {
        STETypeAndData steTypeAndData = new STETypeAndData(
                STEDataType.SLIDE_IN_CACHE,
                new SlideInCacheData()
                        .index(index).status(1)
        );
        if (slideStateObservable.getValue().renderReady()) {
            singleTimeEvents.updateValue(
                    steTypeAndData
            );
        }
    }

    @Override
    public Integer externalSubscriber() {
        return null;
    }

    @Override
    public void renderReady() {
        IAMReaderState readerState = readerViewModel.getCurrentState();
        IInAppMessage readerContent =
                (IInAppMessage) core.contentHolder().readerContent().getByIdAndType(
                        readerState.iamId,
                        ContentType.IN_APP_MESSAGE
                );
        WebPageConverter converter = new WebPageConverter();
        InAppMessageDownloadManager downloadManager = core.contentLoader().inAppMessageDownloadManager();
        List<String> slides = new ArrayList<>();
        List<Integer> loadedSlides = new ArrayList<>();
        List<Integer> errorSlides = new ArrayList<>();
        for (int i = 0; i < readerContent.actualSlidesCount(); i++) {
            slides.add(converter.replaceSlide(readerContent.slideByIndex(i), readerContent, i));
            int loadStatus = downloadManager.isSlideLoaded(readerContent.id(), i);
            if (loadStatus == 1) {
                loadedSlides.add(i);
            } else if (loadStatus == -1) {
                errorSlides.add(i);
            }
        }
        readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.RENDER_READY);
        slideTimeState.updateSlidesCount(slides.size());
        slideStateObservable.updateValue(
                slideStateObservable
                        .getValue()
                        .copy()
                        .slides(slides)
                        .slidesTotal(slides.size())
                        .renderReady(true)
                        .safeArea(readerState.safeArea)
                        .cardAppearance(readerContent.inAppMessageAppearance().cardAppearance())
                        .contentStatus(2)
        );
        for (Integer slide : loadedSlides) {
            slideLoadSuccess(slide);
        }
        for (Integer slide : errorSlides) {
            slideLoadError(slide);
        }
    }

    private final SessionAssetsIsReadyCallback assetsIsReadyCallback = new SessionAssetsIsReadyCallback() {
        @Override
        public void isReady() {
            core.assetsHolder().removeAssetsIsReadyCallback(assetsIsReadyCallback);
            IAMReaderState state = readerViewModel.getCurrentState();
            if (state == null || state.iamId == null) return;
            readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.ASSETS_LOADED);
            InAppMessageDownloadManager downloadManager = core.contentLoader().inAppMessageDownloadManager();
            downloadManager.addInAppMessageTask(state.iamId, null);
        }

        @Override
        public void assetsIsLoading() {
            readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.ASSETS_LOADING);
            readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.LOADING);
        }

        @Override
        public void error() {
            try {
                Log.e("RemoveTimeoutCallback", "error");
                handler.removeCallbacks(contentFailedByTimeout);
            } catch (Exception e) {
            }
            core.assetsHolder().removeAssetsIsReadyCallback(assetsIsReadyCallback);
            readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.ASSETS_FAILED);
            readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.FAILED);
        }
    };

    @Override
    public void reloadContent() {
        try {
            handler.removeCallbacks(contentFailedByTimeout);
            handler.postDelayed(contentFailedByTimeout, failedTimeout);
        } catch (Exception e) {
        }
        readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.ASSETS_LOADING);
        readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.LOADING);
        IAMReaderState state = readerViewModel.getCurrentState();
        InAppMessageDownloadManager downloadManager = core.contentLoader().inAppMessageDownloadManager();
        int loadStatus = downloadManager.isSlidesLoaded(state.iamId);
        if (loadStatus == -1) {
            downloadManager.removeInAppMessageTask(state.iamId);
        } else if (core.assetsHolder().assetsIsDownloaded()) {
            IReaderContent readerContent =
                    core.contentHolder().readerContent().getByIdAndType(
                            state.iamId,
                            ContentType.IN_APP_MESSAGE
                    );
            if (downloadManager.concreteSlidesLoaded(
                    readerContent,
                    new HashSet<>(
                            Collections.singletonList(0)
                    )
            )) {
                readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.LOADED);
                try {
                    Log.e("RemoveTimeoutCallback", "reloadContent");
                    handler.removeCallbacks(contentFailedByTimeout);
                } catch (Exception e) {
                }
            }
        }
        core.assetsHolder().reloadAssets(assetsIsReadyCallback);

    }

    private final int failedTimeout = 30000;
    Runnable contentFailedByTimeout = new Runnable() {
        @Override
        public void run() {
            if (readerViewModel != null) {
                readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.CONTENT_FAILED);
                readerViewModel.updateCurrentLoaderState(IAMReaderLoaderStates.FAILED);
            }
        }
    };

    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public boolean loadContent() {
        try {
            handler.removeCallbacks(contentFailedByTimeout);
            handler.postDelayed(contentFailedByTimeout, failedTimeout);
        } catch (Exception e) {
        }
        IAMReaderState state = readerViewModel.getCurrentState();
        if (state == null || state.iamId == null) return false;
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(
                        state.iamId,
                        ContentType.IN_APP_MESSAGE
                );
        InAppMessageDownloadManager downloadManager = core.contentLoader().inAppMessageDownloadManager();
        if (state.showOnlyIfLoaded) {
            downloadManager.addSubscriber(this);
            if (downloadManager.concreteSlidesLoaded(readerContent, new HashSet<>(
                    Collections.singletonList(0)
            )) && core.assetsHolder().assetsIsDownloaded()) {
                readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.ASSETS_LOADED);
            } else {
                try {
                    Log.e("RemoveTimeoutCallback", "loadContent");
                    handler.removeCallbacks(contentFailedByTimeout);
                } catch (Exception e) {
                }
                readerViewModel.updateCurrentLoadState(IAMReaderLoadStates.CONTENT_FAILED_CLOSE);
                return false;
            }
        } else {
            core.assetsHolder().checkOrAddAssetsIsReadyCallback(assetsIsReadyCallback);
            core.assetsHolder().downloadAssets();
            downloadManager.addSubscriber(this);
        }
        return true;
    }
}
