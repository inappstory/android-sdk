package com.inappstory.sdk.core.banners;

public class BannerPlaceAppearance implements IBannerPlaceAppearance {
    @Override
    public float singleBannerAspectRatio() {
        return 0;
    }


    @Override
    public float cornerRadius() {
        return 0;
    }

    @Override
    public boolean loop() {
        return false;
    }

    @Override
    public boolean autoplay() {
        return false;
    }

    @Override
    public boolean autoplayDelay() {
        return false;
    }

    @Override
    public int animationSpeed() {
        return 0;
    }

}
