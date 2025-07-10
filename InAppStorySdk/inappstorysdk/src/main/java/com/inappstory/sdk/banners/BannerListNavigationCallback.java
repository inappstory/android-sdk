package com.inappstory.sdk.banners;

public interface BannerListNavigationCallback {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
    void onPageSelected(int position);
}
