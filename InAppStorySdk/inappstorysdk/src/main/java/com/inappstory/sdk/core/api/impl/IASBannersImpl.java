package com.inappstory.sdk.core.api.impl;

import androidx.annotation.NonNull;

import com.inappstory.sdk.banners.BannerPlacePreloadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASBanners;
import com.inappstory.sdk.core.banners.BannerPlaceLoadStates;
import com.inappstory.sdk.core.banners.BannerPlaceState;
import com.inappstory.sdk.core.banners.IBannerPlaceViewModel;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.banners.LoadBannerPlaceCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.network.content.usecase.BannerPlaceUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class IASBannersImpl implements IASBanners {
    private final IASCore core;

    public IASBannersImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void loadBannerPlace(final String bannerPlace) {
        BannerPlaceUseCase bannerPlaceUseCase = new BannerPlaceUseCase(core, bannerPlace, null);

        final IBannerPlaceViewModel bannerPagerViewModel =
                core.widgetViewModels().bannerPlaceViewModels().get(bannerPlace);
        bannerPagerViewModel.updateState(
                new BannerPlaceState()
                        .place(bannerPlace)
                        .loadState(
                                BannerPlaceLoadStates.LOADING
                        )
        );
        bannerPlaceUseCase.get(new LoadBannerPlaceCallback() {
            @Override
            public void success(List<IBanner> content) {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .iterationId(UUID.randomUUID().toString())
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannerPlaceLoadStates.EMPTY : BannerPlaceLoadStates.LOADED);
                bannerPagerViewModel.updateState(state);
            }

            @Override
            public void isEmpty() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.EMPTY);
                bannerPagerViewModel.updateState(state);
            }

            @Override
            public void error() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.FAILED);
                bannerPagerViewModel.updateState(state);
            }
        });
    }

    @Override
    public void preload(final String bannerPlace, final BannerPlacePreloadCallback preloadCallback) {
        if (bannerPlace == null) {
            //TODO log error
            return;
        }
        if (preloadCallback != null) {
            if (!Objects.equals(bannerPlace, preloadCallback.bannerPlace())) {
                //TODO log error
                return;
            }
        }
        BannerPlaceUseCase bannerPlaceUseCase = new BannerPlaceUseCase(core, bannerPlace, null);
        final IBannerPlaceViewModel bannerPagerViewModel =
                core.widgetViewModels().bannerPlaceViewModels().get(bannerPlace);
        bannerPagerViewModel.updateState(
                bannerPagerViewModel
                        .getCurrentBannerPagerState()
                        .loadState(
                                BannerPlaceLoadStates.LOADING
                        )
        );
        bannerPlaceUseCase.get(new LoadBannerPlaceCallback() {
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
                    bannerData.add(new BannerData(banner.id(), bannerPlace));
                }
                if (preloadCallback != null) {
                    preloadCallback.bannerPlaceLoaded(content.size(), bannerData);
                }
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .iterationId(UUID.randomUUID().toString())
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannerPlaceLoadStates.EMPTY : BannerPlaceLoadStates.LOADED);
                bannerPagerViewModel.updateState(state);

                List<IBannerViewModel> bannerViewModels = bannerPagerViewModel.getBannerViewModels();
                for (int i = 0; i < bannerViewModels.size(); i++) {
                    bannerViewModels.get(i).loadContent(i == 0, preloadCallback);
                }
            }

            @Override
            public void isEmpty() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.EMPTY);
                bannerPagerViewModel.updateState(state);
            }

            @Override
            public void error() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.FAILED);
                bannerPagerViewModel.updateState(state);
            }
        });
    }
}
