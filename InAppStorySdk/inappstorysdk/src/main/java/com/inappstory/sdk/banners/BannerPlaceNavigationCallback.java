package com.inappstory.sdk.banners;

public interface BannerPlaceNavigationCallback {
    void onPageScrolled(int position, int total, float positionOffset, int positionOffsetPixels);
    void onPageSelected(int position, int total);
}
