package com.inappstory.sdk.core.api.impl;

import android.util.Log;

import com.inappstory.sdk.banners.BannerLoadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASBanners;
import com.inappstory.sdk.core.banners.BannerPagerLoadStates;
import com.inappstory.sdk.core.banners.BannerPagerState;
import com.inappstory.sdk.core.banners.IBannerPagerViewModel;
import com.inappstory.sdk.core.banners.LoadBannerPlaceCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.network.content.usecase.BannerPlaceUseCase;

import java.util.ArrayList;
import java.util.List;

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

        final IBannerPagerViewModel bannerPagerViewModel =
                core.widgetViewModels().bannerPlaceViewModels().get(bannerPlace);
        bannerPagerViewModel.updateState(
                bannerPagerViewModel
                        .getCurrentBannerPagerState()
                        .loadState(
                                BannerPagerLoadStates.LOADING
                        )
        );
        Log.e("bannerPlace", "loadBannerPlace " + bannerPagerViewModel);
        bannerPlaceUseCase.get(new LoadBannerPlaceCallback() {
            @Override
            public void success(List<IBanner> content) {
                BannerPagerState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannerPagerLoadStates.EMPTY : BannerPagerLoadStates.LOADED);
                bannerPagerViewModel.updateState(state);
            }

            @Override
            public void isEmpty() {
                BannerPagerState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPagerLoadStates.EMPTY);
                bannerPagerViewModel.updateState(state);
            }

            @Override
            public void error() {
                BannerPagerState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPagerLoadStates.FAILED);
                bannerPagerViewModel.updateState(state);
            }
        });
    }

    @Override
    public void callback(BannerLoadCallback callback) {

    }
}
