package com.inappstory.sdk.banners.ui.carousel;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.inappstory.sdk.stories.ui.reader.BothSideViewPager;

public class BannerViewPager extends BothSideViewPager {
    public BannerViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public BannerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {

        super.setAdapter(adapter);
    }

    public static abstract class PageChangeListener implements OnPageChangeListener {
    }


    private void init() {}
}
