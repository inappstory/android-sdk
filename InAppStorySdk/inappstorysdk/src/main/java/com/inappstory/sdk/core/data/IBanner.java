package com.inappstory.sdk.core.data;

import com.inappstory.sdk.core.banners.IBannerPlaceAppearance;

public interface IBanner extends IReaderContent {
    int id();
    boolean hasLimit();
    long frequencyLimit();
    long displayFrom();
    long displayTo();
    IBannerPlaceAppearance bannerAppearance();
}
