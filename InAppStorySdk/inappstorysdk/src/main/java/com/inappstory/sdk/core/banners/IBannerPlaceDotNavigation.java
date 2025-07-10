package com.inappstory.sdk.core.banners;

import android.content.Context;
import android.view.View;

public interface IBannerPlaceDotNavigation {
    View getView(Context context);

    BannerDotNavigationPosition position();

    void onBannerItemChanged(
            View view,
            int position
    );

    void onBannerScrolled(
            View view,
            int position,
            float positionOffset,
            int positionOffsetPixels
    );
}
