package com.inappstory.sdk.core.banners;

import android.graphics.drawable.Drawable;

import com.inappstory.sdk.inappmessage.ui.appearance.IReaderBackground;

public interface IBannerPlaceAppearance {
    float singleBannerAspectRatio();

    float cornerRadius(); // in pt

    String backgroundColor();

    Drawable backgroundDrawable();
}
