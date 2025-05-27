package com.inappstory.sdk.core.banners;

import android.content.Context;
import android.view.View;

public interface IBannerPlaceArrowsNavigation {
    View getLeftView(Context context);

    View getRightView(Context context);

    BannerDotNavigationPosition position();

    void onBannerItemChanged(
            View leftView,
            View rightView,
            int position
    );

    void onBannerScrolled(
            View leftView,
            View rightView,
            int position,
            float positionOffset,
            int positionOffsetPixels
    );
}
