package com.inappstory.sdk.banners.ui.place;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.stories.ui.reader.BothSideViewPager;

public class BannerPager  extends BothSideViewPager {
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
