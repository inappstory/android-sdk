package com.inappstory.sdk.banners;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerPagerState;
import com.inappstory.sdk.core.banners.BannerState;
import com.inappstory.sdk.core.banners.IBannerPagerViewModel;
import com.inappstory.sdk.stories.utils.Observer;

public class BannerList extends FrameLayout implements Observer<BannerPagerState> {
    private BannerPager bannerPager;
    private IBannerPagerViewModel bannerPagerViewModel;
    private String bannerPlace;
    private IASCore core;
    private AppearanceManager appearanceManager;

    void setBannerPlace(final String bannerPlace) {
        this.bannerPlace = bannerPlace;
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerList.this.core = core;
                if (bannerPagerViewModel != null)
                    bannerPagerViewModel.removeSubscriber(BannerList.this);
                bannerPagerViewModel = core
                        .widgetViewModels()
                        .bannerPlaceViewModels()
                        .get(bannerPlace);
                bannerPagerViewModel.addSubscriber(BannerList.this);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        if (bannerPagerViewModel != null)
            bannerPagerViewModel.addSubscriber(BannerList.this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (bannerPagerViewModel != null)
            bannerPagerViewModel.removeSubscriber(BannerList.this);
        super.onDetachedFromWindow();
    }

    public BannerList(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BannerList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.cs_banner_widget, this);
        bannerPager = findViewById(R.id.banner_pager);
    }

    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
    }


    @Override
    public void onUpdate(final BannerPagerState newValue) {
        if (newValue == null) return;
        if (bannerPagerViewModel == null) return;
        switch (newValue.loadState()) {
            case EMPTY:
            case FAILED:
            case NONE:
            case LOADING:
                //TODO ?
                break;
            case LOADED:
                bannerPager.setAdapter(
                        new BannerPagerAdapter(
                                appearanceManager,
                                core,
                                newValue.getItems(),
                                bannerPlace
                        )
                );

                break;
        }
    }
}
