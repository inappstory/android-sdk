package com.inappstory.sdk.banners;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.banners.ICustomBannerPlace;
import com.inappstory.sdk.core.data.IBanner;

import java.util.List;


public class BannerPagerAdapter extends PagerAdapter {
    private final List<IBanner> banners;
    private final IASCore core;
    private final String bannerPlace;
    private final float itemWidth;
    private final boolean loop;
    private final float bannerRadius;

    public BannerPagerAdapter(
            IASCore core,
            @NonNull List<IBanner> banners,
            String bannerPlace,
            boolean loop,
            float itemWidth,
            float bannerRadius
    ) {
        this.itemWidth = itemWidth;
        this.banners = banners;
        this.bannerPlace = bannerPlace;
        this.loop = loop;
        this.core = core;
        this.bannerRadius = bannerRadius;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        BannerView bannerView = new BannerView(container.getContext());
        bannerView.setBannerRadius(bannerRadius);
        bannerView.setTag("banner_" + position);
        Log.e("BannerPagerAdapter", "instantiateItem " + position);
        IBanner banner = banners.get(position % banners.size());
        int bannerId = banner.id();
        IBannerViewModel bannerViewModel = core
                .widgetViewModels()
                .bannerViewModels()
                .get(
                        bannerId,
                        bannerPlace
                );
        bannerView.viewModel(
                bannerViewModel
        );
        bannerViewModel.loadContent();
        container.addView(bannerView);
        return bannerView;
    }

    @Override
    public float getPageWidth(int position) {
        return itemWidth;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.e("BannerPagerAdapter", "destroyItem " + position);
        if (object instanceof BannerView)
            container.removeView((BannerView) object);
    }

    @Override
    public int getCount() {
        return (loop ? 500 : 1) * banners.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
