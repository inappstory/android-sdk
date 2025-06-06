package com.inappstory.sdk.core.banners;

public interface IBannerPlaceAppearance {
    float singleBannerAspectRatio();

    float cornerRadius(); // in pt

    boolean loop(); // default = true?

    boolean autoplay(); // default = false?

    int autoplayDelay(); // default = 1000ms?

    int animationSpeed(); // in ms, default = 300ms
}
