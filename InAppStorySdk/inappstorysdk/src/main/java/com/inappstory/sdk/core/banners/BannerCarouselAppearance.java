package com.inappstory.sdk.core.banners;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.IReaderBackground;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.ReaderBackgroundSettings;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;

public class BannerCarouselAppearance implements IBannerCarouselAppearance {
    private Float singleBannerAspectRatio = 2f;
    private Float cornerRadius;
    private String backgroundColor;
    private IReaderBackground background;

    public BannerCarouselAppearance(Map<String, Object> appearanceMap) {
        if (appearanceMap == null) return;
        NumberUtils numberUtils = new NumberUtils();
        singleBannerAspectRatio = numberUtils.convertNumberToFloat(appearanceMap.get("content_ratio"));
        cornerRadius = numberUtils.convertNumberToFloat(appearanceMap.get("corner_radius"));

        String backgroundColorKey = "background_color";
        String backgroundKey = "background";
        if (appearanceMap.containsKey(backgroundColorKey)) {
            backgroundColor = (String) appearanceMap.get(backgroundColorKey);
        }
        if (appearanceMap.containsKey(backgroundKey)) {
            background = new ReaderBackgroundSettings(
                    (Map<String, Object>) appearanceMap.get(backgroundKey)
            );
        }
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
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFF";
    }

    @Override
    public Drawable backgroundDrawable() {
        if (background != null) return background.getBackgroundDrawable();
        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(ColorUtils.parseColorRGBA(backgroundColor()));
        return drawable;
    }


}
