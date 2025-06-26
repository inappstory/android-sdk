package com.inappstory.sdk.core.banners;

public interface ICustomBannerPlace {
    int bannersOnScreen(); // default = 1

    int nextBannerOffset(); // in dp, default = 0dp

    int prevBannerOffset(); // in dp, default = 0dp

    int bannersGap(); // in dp, default = 0dp

    int cornerRadius(); // in dp, default = 0dp

    boolean loop(); // default = false

    int animationSpeed(); //is ms, default = 300ms
}
