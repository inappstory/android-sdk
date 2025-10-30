package com.inappstory.sdk.core.banners;


public interface IBannerPlaceState extends IBannerWidgetState{
    Integer currentIndex();

    IBannerPlaceState currentIndex(Integer currentIndex);
}
