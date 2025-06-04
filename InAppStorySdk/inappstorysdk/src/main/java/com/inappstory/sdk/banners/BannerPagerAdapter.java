package com.inappstory.sdk.banners;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IBanner;

import java.util.List;


public class BannerPagerAdapter extends PagerAdapter {
    private final List<IBanner> banners;
    private final IASCore core;
    private final String bannerPlace;
    private final AppearanceManager appearanceManager;

    public BannerPagerAdapter(
            AppearanceManager appearanceManager,
            IASCore core,
            @NonNull List<IBanner> banners,
            String bannerPlace
    ) {
        this.appearanceManager = appearanceManager;
        this.banners = banners;
        this.bannerPlace = bannerPlace;
        this.core = core;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        BannerView bannerView = new BannerView(container.getContext());
        int bannerId = banners.get(position % banners.size()).id();
        bannerView.setAppearanceManager(appearanceManager);
        bannerView.viewModel(
                core
                        .widgetViewModels()
                        .bannerViewModels()
                        .get(
                                bannerId,
                                bannerPlace
                        )
        );
        container.addView(bannerView);
        return bannerView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof BannerView)
            container.removeView((BannerView) object);
    }

    @Override
    public int getCount() {
        return 100 * banners.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
