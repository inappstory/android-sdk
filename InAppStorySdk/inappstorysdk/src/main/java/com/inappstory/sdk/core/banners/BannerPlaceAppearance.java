package com.inappstory.sdk.core.banners;

import androidx.annotation.NonNull;

import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;
import java.util.Objects;

public class BannerPlaceAppearance implements IBannerPlaceAppearance {
    private Float singleBannerAspectRatio = 2f;
    private Float cornerRadius;
    private boolean loop = true;
    private boolean autoplay = false;
    private Integer autoplayDelay = 1000;
    private Integer animationSpeed = 300;

    public BannerPlaceAppearance(Map<String, Object> appearanceMap) {
        if (appearanceMap == null) return;
        NumberUtils numberUtils = new NumberUtils();
        singleBannerAspectRatio = numberUtils.convertNumberToFloat(appearanceMap.get("content_ratio"));
        cornerRadius = numberUtils.convertNumberToFloat(appearanceMap.get("corner_radius"));
        loop = Objects.equals(appearanceMap.get("loop"), true);
        autoplay = Objects.equals(appearanceMap.get("autoplay"), false);
        autoplayDelay = numberUtils.convertNumberToInt(appearanceMap.get("autoplay_delay"));
        animationSpeed = numberUtils.convertNumberToInt(appearanceMap.get("animation_speed"));
    }

    @Override
    public float singleBannerAspectRatio() {
        return singleBannerAspectRatio != null ? singleBannerAspectRatio : 2f;
    }


    @Override
    public float cornerRadius() {
        return cornerRadius != null ? cornerRadius : 16f;
    }

    @Override
    public boolean loop() {
        return loop;
    }

    @Override
    public boolean autoplay() {
        return autoplay;
    }

    @Override
    public int autoplayDelay() {
        return autoplayDelay != null ? autoplayDelay : 1000;
    }

    @Override
    public int animationSpeed() {
        return animationSpeed != null ? animationSpeed : 300;
    }

}
