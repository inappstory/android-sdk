package com.inappstory.sdk.domain;

import com.inappstory.sdk.core.banners.BannerPagerViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerViewModelsHolder;

public interface IWidgetsViewModels {
    BannerViewModelsHolder bannerViewModels();
    BannerPagerViewModelsHolder bannerPlaceViewModels();
}
