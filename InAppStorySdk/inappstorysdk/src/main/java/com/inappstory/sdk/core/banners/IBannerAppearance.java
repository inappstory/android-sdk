package com.inappstory.sdk.core.banners;

import android.graphics.drawable.Drawable;

public interface IBannerAppearance {
    float singleBannerAspectRatio();

    float cornerRadius(); // in pt

    String backgroundColor();

    Drawable backgroundDrawable();
}
