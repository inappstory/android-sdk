package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.InAppStoryManager.IAS_ERROR_TAG;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.banners.BannerPlacePreloadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASBanners;
import com.inappstory.sdk.banners.BannerPlaceLoadSettings;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.banners.BannerPlaceLoadStates;
import com.inappstory.sdk.core.banners.BannerPlaceState;
import com.inappstory.sdk.core.banners.IBannerPlaceViewModel;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.banners.BannerPlaceUseCaseCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.network.content.usecase.BannerPlaceUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;

import java.util.ArrayList;
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
                    IAS_ERROR_TAG,
                    "Incorrect settings for banner place"
            );
            return;
        }
        final String placeId = settings.placeId();
        final String uniqueId = settings.uniqueId() != null ? settings.uniqueId() : "";
        if (settingsHolder.anonymous()) {
            final IBannerPlaceViewModel bannerPagerViewModel =
                    core.widgetViewModels().bannerPlaceViewModels().getOrCreateWithCopy(uniqueId, placeId);
            bannerPagerViewModel.updateState(
                    bannerPagerViewModel.getCurrentBannerPlaceState()
                            .copy()
                            .items(new ArrayList<IBanner>())
                            .loadState(
                                    BannerPlaceLoadStates.EMPTY)
            );
            if (uniqueId.isEmpty()) {
                updateStateForAllRelatives(
                        placeId,
                        new ArrayList<IBanner>(),
                        null,
                        BannerPlaceLoadStates.EMPTY
                );
            }
            InAppStoryManager.showELog(
                    IAS_ERROR_TAG,
                    "Banners are unavailable for anonymous mode"
            );
            return;
        }
        BannerPlaceUseCase bannerPlaceUseCase = new BannerPlaceUseCase(
                core,
                placeId,
                settings.tags()
        );

        final IBannerPlaceViewModel bannerPagerViewModel =
                core.widgetViewModels().bannerPlaceViewModels().getOrCreateWithCopy(uniqueId, placeId);
        bannerPagerViewModel.updateState(
                new BannerPlaceState()
                        .place(placeId)
                        .tags(settings.tags())
                        .loadState(
                                BannerPlaceLoadStates.LOADING
                        )
        );
        bannerPlaceUseCase.get(new BannerPlaceUseCaseCallback() {
            @Override
            public void success(List<IBanner> content) {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .iterationId(UUID.randomUUID().toString())
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannerPlaceLoadStates.EMPTY : BannerPlaceLoadStates.LOADED);
                bannerPagerViewModel.updateState(state);
                if (uniqueId.isEmpty()) {
                    updateStateForAllRelatives(
                            placeId,
                            content,
                            null,
                            content.isEmpty() ? BannerPlaceLoadStates.EMPTY : BannerPlaceLoadStates.LOADED
                    );
                }
            }

            @Override
            public void isEmpty() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.EMPTY);
                bannerPagerViewModel.updateState(state);
                if (uniqueId.isEmpty()) {
                    updateStateForAllRelatives(
                            placeId,
                            new ArrayList<IBanner>(),
                            null,
                            BannerPlaceLoadStates.EMPTY
                    );
                }
            }

            @Override
            public void error() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.FAILED);
                bannerPagerViewModel.updateState(state);
                if (uniqueId.isEmpty()) {
                    updateStateForAllRelatives(
                            placeId,
                            new ArrayList<IBanner>(),
                            null,
                            BannerPlaceLoadStates.FAILED
                    );
                }
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
        final IBannerPlaceViewModel bannerPlaceViewModel =
                core.widgetViewModels().bannerPlaceViewModels().getOrCreateWithCopy(uniqueId, placeId);
        bannerPlaceViewModel.updateState(
                new BannerPlaceState()
                        .place(placeId)
                        .tags(settings.tags())
                        .loadState(
                                BannerPlaceLoadStates.LOADING
                        )
        );
        if (uniqueId.isEmpty()) {
            updateStateForAllRelatives(
                    placeId,
                    null,
                    settings.tags(),
                    BannerPlaceLoadStates.LOADING
            );
        }
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
                BannerPlaceState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .iterationId(UUID.randomUUID().toString())
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannerPlaceLoadStates.EMPTY : BannerPlaceLoadStates.LOADED);
                bannerPlaceViewModel.updateState(state);

                if (uniqueId.isEmpty()) {
                    updateStateForAllRelatives(
                            placeId,
                            content,
                            null,
                            content.isEmpty() ? BannerPlaceLoadStates.EMPTY : BannerPlaceLoadStates.LOADED
                    );
                }
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
                BannerPlaceState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.EMPTY);
                bannerPlaceViewModel.updateState(state);
                if (uniqueId.isEmpty()) {
                    updateStateForAllRelatives(
                            placeId,
                            new ArrayList<IBanner>(),
                            null,
                            BannerPlaceLoadStates.EMPTY
                    );
                }
            }

            @Override
            public void error() {
                BannerPlaceState state = bannerPlaceViewModel.getCurrentBannerPlaceState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.FAILED);
                bannerPlaceViewModel.updateState(state);
                if (uniqueId.isEmpty()) {
                    updateStateForAllRelatives(
                            placeId,
                            new ArrayList<IBanner>(),
                            null,
                            BannerPlaceLoadStates.FAILED
                    );
                }
            }
        });
    }

    private void updateStateForAllRelatives(
            String bannerPlace,
            List<IBanner> items,
            List<String> tags,
            BannerPlaceLoadStates loadState
    ) {
        Set<IBannerPlaceViewModel> bannerPlaceViewModels = core.
                widgetViewModels().
                bannerPlaceViewModels().
                getNonEmptyByBannerPlace(bannerPlace);
        for (IBannerPlaceViewModel bannerPlaceViewModel : bannerPlaceViewModels) {
            BannerPlaceState state = bannerPlaceViewModel.getCurrentBannerPlaceState().copy();
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
