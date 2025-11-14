package com.inappstory.sdk.core.banners;


public interface IBannerCarouselState extends IBannerWidgetState{
    Integer currentIndex();

    IBannerCarouselState currentIndex(Integer currentIndex);
}
