package com.inappstory.sdk.core.api.impl;


import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.banners.BannerPlacePreloadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASBanners;
import com.inappstory.sdk.banners.BannerPlaceLoadSettings;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.banners.BannersWidgetLoadStates;
import com.inappstory.sdk.core.banners.BannerCarouselState;
import com.inappstory.sdk.core.banners.IBannerWidgetState;
import com.inappstory.sdk.core.banners.IBannersWidgetViewModel;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.banners.BannerPlaceUseCaseCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.network.content.usecase.BannerPlaceUseCase;
import com.inappstory.sdk.banners.BannerData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class IASBannersImpl implements IASBanners {
    private final IASCore core;

    public IASBannersImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void loadBannerPlace(final BannerPlaceLoadSettings settings) {
        final IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());

        if (settings == null || settings.placeId() == null || settings.placeId().isEmpty()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Incorrect settings for banner place"
            );
            return;
        }
        final String placeId = settings.placeId();
        final String uniqueId = settings.uniqueId() != null ? settings.uniqueId() : "";
        final IBannersWidgetViewModel bannerPlaceViewModel = core
                .widgetViewModels()
                .bannerPlaceViewModels()
                .getOrCreateContentPlaceViewModel(placeId);
        if (settingsHolder.anonymous()) {

            bannerPlaceViewModel.updateState(
                    bannerPlaceViewModel.getCurrentBannerPlaceState()
                            .copy()
                            .items(new ArrayList<>())
                            .loadState(
                                    BannersWidgetLoadStates.EMPTY)
            );
            updateStateForAllRelatives(
                    placeId,
                    uniqueId,
                    new ArrayList<IBanner>(),
                    null,
                    BannersWidgetLoadStates.EMPTY
            );
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Banners are unavailable for anonymous mode"
            );
            return;
        }
        BannerPlaceUseCase bannerPlaceUseCase = new BannerPlaceUseCase(
                core,
                placeId,
                settings.tags()
        );
        bannerPlaceViewModel.updateState(
                new BannerCarouselState()
                        .placeId(placeId)
                        .tags(settings.tags())
                        .loadState(
                                BannersWidgetLoadStates.LOADING
                        )
        );
        updateStateForAllRelatives(
                placeId,
                uniqueId,
                null,
                settings.tags(),
                BannersWidgetLoadStates.LOADING
        );
        bannerPlaceUseCase.get(new BannerPlaceUseCaseCallback() {
            @Override
            public void success(List<IBanner> content) {
                IBannerWidgetState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .iterationId(UUID.randomUUID().toString())
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannersWidgetLoadStates.EMPTY : BannersWidgetLoadStates.LOADED);
                bannerPlaceViewModel.updateState(state);

                updateStateForAllRelatives(
                        placeId,
                        uniqueId,
                        content,
                        null,
                        content.isEmpty() ? BannersWidgetLoadStates.EMPTY : BannersWidgetLoadStates.LOADED
                );
            }

            @Override
            public void isEmpty() {
                IBannerWidgetState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannersWidgetLoadStates.EMPTY);
                bannerPlaceViewModel.updateState(state);
                updateStateForAllRelatives(
                        placeId,
                        uniqueId,
                        new ArrayList<IBanner>(),
                        null,
                        BannersWidgetLoadStates.EMPTY
                );
            }

            @Override
            public void error() {
                IBannerWidgetState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .items(new ArrayList<>())
                        .loadState(
                                BannersWidgetLoadStates.FAILED);
                bannerPlaceViewModel.updateState(state);
                updateStateForAllRelatives(
                        placeId,
                        uniqueId,
                        new ArrayList<IBanner>(),
                        null,
                        BannersWidgetLoadStates.FAILED
                );
            }
        });
    }

    @Override
    public void preload(final BannerPlaceLoadSettings settings, final BannerPlacePreloadCallback preloadCallback) {
        if (settings == null || settings.placeId() == null || settings.placeId().isEmpty()) {
            //TODO log error
            return;
        }
        final String placeId = settings.placeId();
        final String uniqueId = settings.uniqueId() != null ? settings.uniqueId() : "";
        if (preloadCallback != null) {
            if (!Objects.equals(placeId, preloadCallback.bannerPlace())) {
                //TODO log error
                return;
            }
        }
        BannerPlaceUseCase bannerPlaceUseCase = new BannerPlaceUseCase(
                core,
                placeId,
                settings.tags()
        );
        final IBannersWidgetViewModel bannerPlaceViewModel = core
                .widgetViewModels()
                .bannerPlaceViewModels()
                .getOrCreateContentPlaceViewModel(placeId);
        bannerPlaceViewModel.updateState(
                new BannerCarouselState()
                        .placeId(placeId)
                        .tags(settings.tags())
                        .loadState(
                                BannersWidgetLoadStates.LOADING
                        )
        );
        updateStateForAllRelatives(
                placeId,
                uniqueId,
                null,
                settings.tags(),
                BannersWidgetLoadStates.LOADING
        );
        bannerPlaceUseCase.get(new BannerPlaceUseCaseCallback() {
            @Override
            public void success(List<IBanner> content) {
                if (content == null) {
                    if (preloadCallback != null) {
                        preloadCallback.loadError();
                    }
                    return;
                }
                List<BannerData> bannerData = new ArrayList<>();
                for (IBanner banner : content) {
                    bannerData.add(new BannerData(banner.id(), placeId, banner.slideEventPayload(0)));
                }
                if (preloadCallback != null) {
                    preloadCallback.bannerPlaceLoaded(content.size(), bannerData);
                }
                IBannerWidgetState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .iterationId(UUID.randomUUID().toString())
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannersWidgetLoadStates.EMPTY : BannersWidgetLoadStates.LOADED);
                bannerPlaceViewModel.updateState(state);
                updateStateForAllRelatives(
                        placeId,
                        uniqueId,
                        content,
                        null,
                        content.isEmpty() ? BannersWidgetLoadStates.EMPTY : BannersWidgetLoadStates.LOADED
                );
                List<IBannerViewModel> bannerViewModels = bannerPlaceViewModel.getBannerViewModels();
                for (IBannerViewModel bannerViewModel : bannerViewModels) {
                    bannerViewModel.loadContent(
                            bannerViewModel.isFirst(),
                            preloadCallback
                    );
                }
            }

            @Override
            public void isEmpty() {
                IBannerWidgetState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .items(new ArrayList<>())
                        .loadState(
                                BannersWidgetLoadStates.EMPTY);
                bannerPlaceViewModel.updateState(state);
                updateStateForAllRelatives(
                        placeId,
                        uniqueId,
                        new ArrayList<IBanner>(),
                        null,
                        BannersWidgetLoadStates.EMPTY
                );
            }

            @Override
            public void error() {
                IBannerWidgetState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .items(new ArrayList<>())
                        .loadState(
                                BannersWidgetLoadStates.FAILED);
                bannerPlaceViewModel.updateState(state);
                updateStateForAllRelatives(
                        placeId,
                        uniqueId,
                        new ArrayList<>(),
                        null,
                        BannersWidgetLoadStates.FAILED
                );
            }
        });
    }

    private void updateStateForAllRelatives(
            String placeId,
            String uniqueId,
            List<IBanner> items,
            List<String> tags,
            BannersWidgetLoadStates loadState
    ) {
        Set<IBannersWidgetViewModel> bannerPlaceViewModels = new HashSet<>();
        if (uniqueId == null || uniqueId.isEmpty()) {
            bannerPlaceViewModels.addAll(
                    core.
                            widgetViewModels().
                            bannerPlaceViewModels().
                            getNonEmptyByPlaceId(placeId)
            );
        } else {
            IBannersWidgetViewModel placeViewModel = core.widgetViewModels().bannerPlaceViewModels().get(uniqueId);
            if (placeViewModel != null && placeViewModel.placeId().equals(placeId))
                bannerPlaceViewModels.add(
                        placeViewModel
                );
        }
        for (IBannersWidgetViewModel bannerPlaceViewModel : bannerPlaceViewModels) {
            IBannerWidgetState state = bannerPlaceViewModel.getCurrentBannerPlaceState().copy();
            if (tags != null) {
                state.tags(tags);
            }
            if (items != null) {
                state
                        .items(items)
                        .iterationId(UUID.randomUUID().toString());
            }
            state.loadState(loadState);
            bannerPlaceViewModel.updateState(state);
        }
    }
}
