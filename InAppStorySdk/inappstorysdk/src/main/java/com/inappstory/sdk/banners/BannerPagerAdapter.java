package com.inappstory.sdk.banners;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.banners.BannerLoadStates;
import com.inappstory.sdk.core.banners.BannerState;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.stories.utils.Observer;

import java.util.List;
import java.util.Objects;


public class BannerPagerAdapter extends PagerAdapter implements Observer<BannerState> {
    private final List<IBanner> banners;
    private final IASCore core;
    private final String bannerPlace;
    private final float itemWidth;

    public boolean isLoop() {
        return loop;
    }

    private final boolean loop;
    private final float bannerRadius;
    private final String iterationId;
    private final ICustomBannerPlaceholder bannerPlaceholderCreator;

    public BannerPagerAdapter(
            IASCore core,
            @NonNull List<IBanner> banners,
            String bannerPlace,
            ICustomBannerPlaceholder bannerPlaceholderCreator,
            BannerPlaceLoadCallback bannerPlaceLoadCallback,
            String iterationId,
            boolean loop,
            float itemWidth,
            float bannerRadius
    ) {
        this.itemWidth = itemWidth;
        this.bannerPlaceholderCreator = bannerPlaceholderCreator;
        this.listLoadCallback = bannerPlaceLoadCallback;
        this.banners = banners;
        this.iterationId = iterationId;
        this.bannerPlace = bannerPlace;
        this.loop = loop;
        this.core = core;
        this.bannerRadius = bannerRadius;
        subscribeToFirst();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        BannerView bannerView = new BannerView(container.getContext());
        bannerView.setLoadingPlaceholder(bannerPlaceholderCreator.onCreate(container.getContext()));
        bannerView.setBannerRadius(bannerRadius);
        bannerView.setTag("banner_" + position);
        Log.e("BannerPagerAdapter", "instantiateItem " + position);
        IBanner banner = banners.get(position % banners.size());
        int bannerId = banner.id();
        IBannerViewModel bannerViewModel  = core
                .widgetViewModels().bannerPlaceViewModels().get(bannerPlace).getBannerViewModel(bannerId);
        bannerViewModel.iterationId(iterationId);
        bannerView.viewModel(
                bannerViewModel
        );
        bannerViewModel.loadContent();
        container.addView(bannerView);
        return bannerView;
    }

    private BannerPlaceLoadCallback listLoadCallback;

    public void subscribeToFirst() {
        if (banners.isEmpty()) return;
        IBanner banner = banners.get(0);
        int bannerId = banner.id();

        IBannerViewModel bannerViewModel  = core
                .widgetViewModels().bannerPlaceViewModels().get(bannerPlace).getBannerViewModel(bannerId);
        bannerViewModel.addSubscriber(this);
    }

    public void unsubscribeFromFirst() {
        if (banners.isEmpty()) return;
        IBanner banner = banners.get(0);
        int bannerId = banner.id();

        IBannerViewModel bannerViewModel  = core
                .widgetViewModels().bannerPlaceViewModels().get(bannerPlace).getBannerViewModel(bannerId);
        bannerViewModel.removeSubscriber(this);
    }

    @Override
    public float getPageWidth(int position) {
        return itemWidth;
    }

    BannerState currentState;

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

    public int getDataCount() {
        return banners.size();
    }

    public int getStartedIndex() {
        return (loop ? 200 : 0) * banners.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void onUpdate(BannerState newValue) {
        if (newValue == null) return;
        if (currentState == null ||
                (newValue.loadState() != currentState.loadState())
        ) {
            if (Objects.requireNonNull(newValue.loadState()) == BannerLoadStates.FAILED) {
                if (listLoadCallback != null)
                    listLoadCallback.firstBannerLoadError(newValue.bannerId(), bannerPlace);
            }
        }
        if (currentState == null ||
                (newValue.slideJSStatus() != currentState.slideJSStatus())
        ) {
            if (newValue.slideJSStatus() == 1) {
                if (listLoadCallback != null)
                    listLoadCallback.firstBannerLoaded(newValue.bannerId(), bannerPlace);
            }

        }
        currentState = newValue;
    }
}
