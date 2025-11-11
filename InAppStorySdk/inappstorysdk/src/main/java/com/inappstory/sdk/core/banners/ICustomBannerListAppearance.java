package com.inappstory.sdk.core.banners;

import android.content.Context;
import android.view.View;

public interface ICustomBannerListAppearance {
    int edgeBannersPadding();

    int bannersGap();

    int cornerRadius(); // in dp, default = 0dp

    int columnCount();

    int orientation();

    View loadingPlaceholder(Context context);
}
