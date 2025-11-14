package com.inappstory.sdk.core.data;

import com.inappstory.sdk.core.banners.IBannerCarouselAppearance;

public interface IBanner extends IReaderContent {
    int id();
    boolean hasLimit();
    long frequencyLimit();
    long displayFrom();
    long displayTo();
    IBannerCarouselAppearance bannerAppearance();
}
