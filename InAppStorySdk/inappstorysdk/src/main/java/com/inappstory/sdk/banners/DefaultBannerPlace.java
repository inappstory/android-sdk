package com.inappstory.sdk.banners;


import android.content.Context;
import android.view.View;

import com.inappstory.sdk.core.banners.ICustomBannerPlace;

public class DefaultBannerPlace implements ICustomBannerPlace {
    @Override
    public int bannersOnScreen() {
        return 1;
    }

    @Override
    public int nextBannerOffset() {
        return 0;
    }

    @Override
    public int prevBannerOffset() {
        return 0;
    }

    @Override
    public int bannersGap() {
        return 0;
    }

    @Override
    public int cornerRadius() {
        return 0;
    }

    @Override
    public boolean loop() {
        return true;
    }

    @Override
    public View loadingPlaceholder(Context context) {
        return null;
    }

}
