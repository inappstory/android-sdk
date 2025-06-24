package com.inappstory.sdk.core.banners;

import androidx.annotation.NonNull;

import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;
import java.util.Objects;

public class BannerPlaceAppearance implements IBannerPlaceAppearance {
    private float singleBannerAspectRatio = 2f;
    private float cornerRadius = 16f;
    private boolean loop = true;
    private boolean autoplay = false;
    private int autoplayDelay = 1000;
    private int animationSpeed = 300;

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
        return singleBannerAspectRatio;
    }


    @Override
    public float cornerRadius() {
        return cornerRadius;
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
        return autoplayDelay;
    }

    @Override
    public int animationSpeed() {
        return animationSpeed;
    }

}
