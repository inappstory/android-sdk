package com.inappstory.sdk.core.banners;

import android.content.Context;
import android.view.View;

public interface ICustomBannerPlace {
    int bannersOnScreen(); // default = 1

    int nextBannerOffset(); // in dp, default = 0dp

    int prevBannerOffset(); // in dp, default = 0dp

    int bannersGap(); // in dp, default = 0dp

    int cornerRadius(); // in dp, default = 0dp

    boolean loop(); // default = false

    View loadingPlaceholder(Context context);
}
