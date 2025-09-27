package com.inappstory.sdk.domain;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.banners.BannerContentViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerPlaceViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerViewModelsHolder;

public class WidgetsViewModels implements IWidgetsViewModels {

    private final BannerPlaceViewModelsHolder bannerPlaceViewModelsHolder;
    private final BannerViewModelsHolder bannerViewModelsHolder;
    private final BannerContentViewModelsHolder bannerContentViewModelsHolder;
    private final IASCore core;

    public WidgetsViewModels(IASCore core) {
        this.core = core;
        bannerPlaceViewModelsHolder = new BannerPlaceViewModelsHolder(core);
        bannerViewModelsHolder = new BannerViewModelsHolder(core);
        bannerContentViewModelsHolder = new BannerContentViewModelsHolder(core);
    }

    @Override
    public BannerPlaceViewModelsHolder bannerPlaceViewModels() {
        return bannerPlaceViewModelsHolder;
    }

    @Override
    public BannerContentViewModelsHolder bannerContentViewModels() {
        return bannerContentViewModelsHolder;
    }

    @Override
    public BannerViewModelsHolder bannerViewModels() {
        return bannerViewModelsHolder;
    }
}
