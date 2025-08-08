package com.inappstory.sdk.banners.ui.place;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class BannerPager extends ViewPager {
    public BannerPager(@NonNull Context context) {
        super(context);
        init();
    }

    public BannerPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public static abstract class PageChangeListener implements OnPageChangeListener {
    }


    private void init() {}
}
