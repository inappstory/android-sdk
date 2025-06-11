package com.inappstory.sdk.banners;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import com.inappstory.sdk.core.banners.IBannerPlaceArrowsNavigation;
import com.inappstory.sdk.core.banners.IBannerPlaceDotNavigation;
import com.inappstory.sdk.core.banners.ICustomBannerPlace;

public class DefaultBannerPlace implements ICustomBannerPlace {
    @Override
    public int bannersOnScreen() {
        return 1;
    }

    @Override
    public int nextBannerOffset() {
        return 24;
    }

    @Override
    public int prevBannerOffset() {
        return 24;
    }

    @Override
    public int bannersGap() {
        return 8;
    }

    @Override
    public int maxHeight() {
        return MATCH_PARENT;
    }

    @Override
    public IBannerPlaceArrowsNavigation arrowsNavigation() {
        return null;
    }

    @Override
    public IBannerPlaceDotNavigation dotNavigation() {
        return null;
    }
}
