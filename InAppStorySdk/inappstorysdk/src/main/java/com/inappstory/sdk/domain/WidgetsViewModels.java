package com.inappstory.sdk.domain;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.banners.BannerPagerViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerViewModelsHolder;

public class WidgetsViewModels implements IWidgetsViewModels {

    private final BannerPagerViewModelsHolder bannerPagerViewModelsHolder;
    private final BannerViewModelsHolder bannerViewModelsHolder;
    private final IASCore core;

    public WidgetsViewModels(IASCore core) {
        this.core = core;
        bannerPagerViewModelsHolder = new BannerPagerViewModelsHolder(core);
        bannerViewModelsHolder = new BannerViewModelsHolder(core);
    }

    @Override
    public BannerViewModelsHolder bannerViewModels() {
        return bannerViewModelsHolder;
    }

    @Override
    public BannerPagerViewModelsHolder bannerPlaceViewModels() {
        return bannerPagerViewModelsHolder;
    }
}
