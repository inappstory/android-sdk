package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.banners.BannerPlacePreloadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.WebPageConvertCallback;
import com.inappstory.sdk.stories.utils.WebPageConverter;

public class BannerContentViewModel implements IBannerContentViewModel {

    private final int bannerId;
    private final String bannerPlace;
    private final Observable<BannerContentState> stateObservable =
            new Observable<>(new BannerContentState());

    private final IASCore core;


    public BannerContentViewModel(
            int bannerId,
            String bannerPlace,
            IASCore core
    ) {
        this.bannerId = bannerId;
        this.bannerPlace = bannerPlace;
        this.core = core;
        stateObservable.setValue(
                new BannerContentState()
                        .bannerId(bannerId)
                        .bannerPlace(bannerPlace)
                        .loadState(BannerLoadStates.EMPTY)
        );
    }

    private String payload;

    @Override
    public BannerContentState getCurrentBannerContentState() {
        return stateObservable.getValue();
    }

    @Override
    public BannerData getCurrentBannerData() {
        return new BannerData(
                bannerId,
                bannerPlace,
                payload
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
                getCurrentBannerContentState().copy().contentStatus(-1).content(null)
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
        final BannerContentState readerState = getCurrentBannerContentState();
        if (readerState.contentStatus() == 1) return;
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(
                        bannerId,
                        ContentType.BANNER
                );
        if (readerContent == null) return;
        payload = readerContent.slideEventPayload(0);
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

    @Override
    public void renderReady() {

    }

    @Override
    public boolean loadContent() {
        return false;
    }

    @Override
    public void addSubscriber(Observer<BannerContentState> observable) {
        this.stateObservable.subscribeAndGetValue(observable);
    }

    @Override
    public void removeSubscriber(Observer<BannerContentState> observable) {
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
    public boolean loadContent(boolean isFirst, BannerPlacePreloadCallback callback) {
        final BannerContentState state = getCurrentBannerContentState();
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
            downloadManager.addBannerTask(bannerId, callback, isFirst);
        }
        return true;
    }

    @Override
    public void clear() {
        stateObservable.setValue(
                new BannerContentState()
                        .bannerId(bannerId)
                        .bannerPlace(bannerPlace)
                        .loadState(BannerLoadStates.EMPTY)
        );
    }
}
