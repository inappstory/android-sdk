package com.inappstory.sdk.core.banners;

import androidx.annotation.NonNull;

import com.inappstory.sdk.banners.BannerWidgetCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.inappmessage.domain.stedata.JsSendApiRequestData;
import com.inappstory.sdk.inappmessage.domain.stedata.STEDataType;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.ContentId;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.SlideLinkObject;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;
import com.inappstory.sdk.stories.utils.WebPageConvertCallback;
import com.inappstory.sdk.stories.utils.WebPageConverter;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.Map;

public class BannerViewModel implements IBannerViewModel {

    private final int bannerId;
    private final String bannerPlace;
    private final Observable<BannerState> stateObservable =
            new Observable<>(new BannerState());

    private final IASCore core;

    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
            new SingleTimeEvent<>();

    public BannerViewModel(int bannerId, String bannerPlace, IASCore core) {
        this.bannerId = bannerId;
        this.bannerPlace = bannerPlace;
        this.core = core;
        stateObservable.setValue(
                new BannerState()
                        .bannerId(bannerId)
                        .bannerPlace(bannerPlace)
                        .loadState(BannerLoadStates.EMPTY)
        );
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
            updateCurrentLoadState(BannerLoadStates.LOADED);
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
        this.stateObservable.subscribe(observable);
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
        updateCurrentLoadState(BannerLoadStates.LOADED);
        stateObservable.updateValue(
                stateObservable
                        .getValue()
                        .copy()
                        .slideJSStatus(1)
        );
    }

    @Override
    public void statisticEvent(final String name, String data, String eventData) {
        if (data != null) {
           /* core.statistic().iamV1().sendWidgetEvent(
                    name,
                    data,
                    iamId,
                    0,
                    1,
                    slideTimeState.totalTime(),
                    slideTimeState.iterationId()
            );*/
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

    @Override
    public void storySendData(String data) {

    }

    @Override
    public void setLocalUserData(String data, boolean sendToServer) {

    }

    @Override
    public String getLocalUserData() {
        return null;
    }

    @Override
    public void shareSlideScreenshotCb(String shareId, boolean result) {

    }
}
