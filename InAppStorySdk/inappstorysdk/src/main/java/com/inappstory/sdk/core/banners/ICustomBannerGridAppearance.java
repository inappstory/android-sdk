package com.inappstory.sdk.core.banners;

import android.content.Context;
import android.view.View;

public interface ICustomBannerGridAppearance {
    int verticalGap();

    int horizontalGap();

    int cornerRadius(); // in dp, default = 0dp

    int columnCount();

    BannerGridAlign align();

    int limit();

    View loadingPlaceholder(Context context);
}
