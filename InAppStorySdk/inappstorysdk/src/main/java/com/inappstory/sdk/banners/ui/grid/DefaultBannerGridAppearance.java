package com.inappstory.sdk.banners.ui.grid;


import android.content.Context;
import android.view.View;


import com.inappstory.sdk.core.banners.BannerGridAlign;
import com.inappstory.sdk.core.banners.ICustomBannerGridAppearance;

public class DefaultBannerGridAppearance implements ICustomBannerGridAppearance {

    @Override
    public int verticalGap() {
        return 8;
    }

    @Override
    public int horizontalGap() {
        return 8;
    }

    @Override
    public int cornerRadius() {
        return 16;
    }

    @Override
    public final int columnCount() {
        return 2;
    }

    @Override
    public BannerGridAlign align() {
        return BannerGridAlign.TOP;
    }

    @Override
    public int limit() {
        return -1;
    }

    @Override
    public View loadingPlaceholder(Context context) {
        return null;
    }
}
