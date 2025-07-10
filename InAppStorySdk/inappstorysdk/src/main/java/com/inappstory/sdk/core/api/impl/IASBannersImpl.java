package com.inappstory.sdk.core.api.impl;

import android.util.Log;

import com.inappstory.sdk.banners.BannerLoadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASBanners;
import com.inappstory.sdk.core.banners.BannerPlaceLoadStates;
import com.inappstory.sdk.core.banners.BannerPlaceState;
import com.inappstory.sdk.core.banners.IBannerPlaceViewModel;
import com.inappstory.sdk.core.banners.LoadBannerPlaceCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.network.content.usecase.BannerPlaceUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IASBannersImpl implements IASBanners {
    private final IASCore core;

    public IASBannersImpl(IASCore core) {
        this.core = core;
    }


    @Override
    public void preload(BannerLoadCallback callback) {

    }

    @Override
    public void loadBannerPlace(final String bannerPlace, BannerLoadCallback callback) {
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
        Log.e("bannerPlace", "loadBannerPlace " + bannerPagerViewModel);
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
    public void callback(BannerLoadCallback callback) {

    }
}
