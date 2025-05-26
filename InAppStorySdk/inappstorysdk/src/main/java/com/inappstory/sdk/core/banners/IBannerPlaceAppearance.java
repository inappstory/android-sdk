package com.inappstory.sdk.core.banners;

public interface IBannerPlaceAppearance {
    float singleBannerAspectRatio();

    int bannersOnScreen();
    // 1 by default ? is it necessary to get from backend?

    int bannersGap(); // in pt, default = 16pt

    float previousBannerPercent();
    //not by Material Design, cause different aligns with another elements, but looks same on different platforms
    // May be it's better to use fixed sizes, or even developer-dependent sizes.

    float nextBannerPercent();
    // same use case

    float cornerRadius(); // in pt

    boolean loop(); // default = true?

    boolean autoplay(); // default = false?

    boolean autoplayDelay(); // default = 1000ms?

    int animationSpeed(); // in ms, default = 300ms

    int bannersTotal();
    // is it necessary (only if we load appearance and list separately)?
    // or list size is enough?

    IBannerPlaceDotNavigation dotNavigation();
    // May be it's better to set both navigation separately in case of using both types at the same time

    IBannerPlaceArrowsNavigation arrowsNavigation();
}
