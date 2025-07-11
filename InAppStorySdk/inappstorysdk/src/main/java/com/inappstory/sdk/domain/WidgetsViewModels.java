package com.inappstory.sdk.domain;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.banners.BannerPlaceViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerViewModelsHolder;

public class WidgetsViewModels implements IWidgetsViewModels {

    private final BannerPlaceViewModelsHolder bannerPlaceViewModelsHolder;
    private final IASCore core;

    public WidgetsViewModels(IASCore core) {
        this.core = core;
        bannerPlaceViewModelsHolder = new BannerPlaceViewModelsHolder(core);
    }

    @Override
    public BannerPlaceViewModelsHolder bannerPlaceViewModels() {
        return bannerPlaceViewModelsHolder;
    }
}
