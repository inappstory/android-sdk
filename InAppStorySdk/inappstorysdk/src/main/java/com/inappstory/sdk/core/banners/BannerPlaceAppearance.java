package com.inappstory.sdk.core.banners;

import androidx.annotation.NonNull;

import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;
import java.util.Objects;

public class BannerPlaceAppearance implements IBannerPlaceAppearance {
    private final float singleBannerAspectRatio;
    private final float cornerRadius;
    private final boolean loop;
    private final boolean autoplay;
    private final int autoplayDelay;
    private final int animationSpeed;

    public BannerPlaceAppearance(@NonNull Map<String, Object> appearanceMap) {
        NumberUtils numberUtils = new NumberUtils();
        singleBannerAspectRatio = numberUtils.convertNumberToFloat(appearanceMap.get("single_banner_aspect_ratio"));
        cornerRadius = numberUtils.convertNumberToFloat(appearanceMap.get("corner_radius"));
        loop = Objects.equals(appearanceMap.get("loop"), true);
        autoplay = Objects.equals(appearanceMap.get("autoplay"), true);
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
