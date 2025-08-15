package com.inappstory.sdk.banners.ui.place;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.banners.ui.banner.BannerView;
import com.inappstory.sdk.core.banners.IBannerPlaceLoadCallback;
import com.inappstory.sdk.banners.ICustomBannerPlaceholder;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerDownloadManager;
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
    private final float iwRatio;
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
            IBannerPlaceLoadCallback bannerPlaceLoadCallback,
            String iterationId,
            boolean loop,
            float iwRatio,
            float itemWidth,
            float bannerRadius
    ) {
        this.itemWidth = itemWidth;
        this.iwRatio = iwRatio;
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
        bannerView.setBannerBackground(banner.bannerAppearance().backgroundDrawable());
        bannerView.setSize(itemWidth, banner.bannerAppearance().singleBannerAspectRatio());
        final int bannerId = banner.id();
        final IBannerViewModel bannerViewModel = core
                .widgetViewModels().bannerPlaceViewModels().get(bannerPlace).getBannerViewModel(bannerId);
        bannerViewModel.iterationId(iterationId);
        bannerView.viewModel(
                bannerViewModel
        );
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerDownloadManager bannerDownloadManager = core.contentLoader().bannerDownloadManager();
                bannerDownloadManager.setMaxPriority(bannerId, false);
                bannerViewModel.loadContent(false, null);
            }
        });

        container.addView(bannerView);
        bannerView.setListLoadCallback(listLoadCallback);
        return bannerView;
    }

    private IBannerPlaceLoadCallback listLoadCallback;

    public void subscribeToFirst() {
        if (banners.isEmpty()) return;
        IBanner banner = banners.get(0);
        int bannerId = banner.id();

        IBannerViewModel bannerViewModel = core
                .widgetViewModels().bannerPlaceViewModels().get(bannerPlace).getBannerViewModel(bannerId);
        bannerViewModel.addSubscriber(this);
    }

    public void clear() {
        this.banners.clear();
    }

    public void unsubscribeFromFirst() {
        if (banners.isEmpty()) return;
        IBanner banner = banners.get(0);
        int bannerId = banner.id();

        IBannerViewModel bannerViewModel = core
                .widgetViewModels().bannerPlaceViewModels().get(bannerPlace).getBannerViewModel(bannerId);
        bannerViewModel.removeSubscriber(this);
    }

    @Override
    public float getPageWidth(int position) {
        return iwRatio;
    }

    BannerState currentState;

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof BannerView) {
            ((BannerView) object).removeListLoadCallback();
            container.removeView((BannerView) object);
        }
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

       /* Log.e("ObserverUpdate", Thread.currentThread().getName() + " PagerAdapter onUpdate " + newValue);
        if (currentState == null ||
                (newValue.loadState() != currentState.loadState())
        ) {
            if (Objects.requireNonNull(newValue.loadState()) == BannerLoadStates.FAILED) {
                if (listLoadCallback != null)
                    listLoadCallback.bannerLoadError(newValue.bannerId(), newValue.bannerIsActive());
            }
        }
        if (currentState == null ||
                (newValue.slideJSStatus() != currentState.slideJSStatus())
        ) {
            if (newValue.slideJSStatus() == 1) {
                if (listLoadCallback != null)
                    listLoadCallback.bannerLoaded(newValue.bannerId(), newValue.bannerIsActive());
            }

        }*/
        //currentState = newValue;
    }
}
