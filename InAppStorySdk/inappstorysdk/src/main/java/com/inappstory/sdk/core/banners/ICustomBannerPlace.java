package com.inappstory.sdk.core.banners;

public interface ICustomBannerPlace {
    int bannersOnScreen(); // default = 1

    float nextBannerOffset(); // in dp, default = 0dp

    float prevBannerOffset(); // in dp, default = 0dp

    int bannersGap(); // in dp, default = 0dp

    IBannerPlaceArrowsNavigation arrowsNavigation();

    IBannerPlaceDotNavigation dotNavigation();
}
