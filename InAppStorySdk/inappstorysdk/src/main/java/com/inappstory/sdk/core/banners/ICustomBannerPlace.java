package com.inappstory.sdk.core.banners;

public interface ICustomBannerPlace {
    int bannersOnScreen(); // default = 1

    int nextBannerOffset(); // in dp, default = 0dp

    int prevBannerOffset(); // in dp, default = 0dp

    int bannersGap(); // in dp, default = 0dp

    int maxHeight(); // in dp, default = -1

    IBannerPlaceArrowsNavigation arrowsNavigation();

    IBannerPlaceDotNavigation dotNavigation();

}
