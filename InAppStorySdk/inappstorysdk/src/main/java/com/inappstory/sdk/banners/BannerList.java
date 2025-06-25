package com.inappstory.sdk.banners;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerPagerState;
import com.inappstory.sdk.core.banners.IBannerPagerViewModel;
import com.inappstory.sdk.core.banners.ICustomBannerPlace;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.List;

public class BannerList extends RelativeLayout implements Observer<BannerPagerState> {
    private BannerPager bannerPager;
    private IBannerPagerViewModel bannerPagerViewModel;
    private String bannerPlace;
    private IASCore core;
    private ICustomBannerPlace customBannerPlace = new DefaultBannerPlace();
    private String lastLaunchedTag = "banner_0";

    BannerPager.PageChangeListener pageChangeListener = new BannerPager.PageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            BannerView currentBannerView = bannerPager.findViewWithTag("banner_" + position);
        }

        @Override
        public void onPageSelected(int position) {
            String newLaunchedTag = "banner_" + position;
            if (!lastLaunchedTag.isEmpty() && !(lastLaunchedTag.equals(newLaunchedTag))) {
                BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
                if (currentBannerView != null) currentBannerView.stopBanner();
            }
            BannerView currentBannerView = bannerPager.findViewWithTag(newLaunchedTag);
            if (currentBannerView != null) {
                currentBannerView.startBanner();
                currentBannerView.resumeBanner();
            }
            lastLaunchedTag = newLaunchedTag;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            String newLaunchedTag = "banner_" +  bannerPager.getCurrentItem();
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (newLaunchedTag.equals(lastLaunchedTag)) {
                    BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
                    if (currentBannerView != null) currentBannerView.resumeBanner();
                }
            }

        }
    };

    public void setBannerPlace(final String bannerPlace) {
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
                Log.e("bannerPlace", "setBannerPlace " + bannerPagerViewModel.toString());
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
        bannerPager.addOnPageChangeListener(pageChangeListener);
        setVisibility(GONE);
    }

    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.customBannerPlace = appearanceManager.csBannerPlaceInterface();
    }

    private int calculateHeight(List<IBanner> banners) {
        if (banners.isEmpty()) return MATCH_PARENT;
        float maxHeightRatio = banners.get(0).bannerAppearance().singleBannerAspectRatio();
        for (int i = 1; i < banners.size(); i++) {
            float tempRatio = banners.get(i).bannerAppearance().singleBannerAspectRatio();
            if (tempRatio < maxHeightRatio) maxHeightRatio = tempRatio;
        }
        float realWidth = calculateItemWidth();
        if (customBannerPlace.maxHeight() == -1)
            return Math.min((int) (realWidth / maxHeightRatio),
                    Sizes.getScreenSize(getContext()).y);
        return Math.min((int) (realWidth / maxHeightRatio),
                Math.min(customBannerPlace.maxHeight(),
                        Sizes.getScreenSize(getContext()).y)
        );
    }

    private float calculateItemWidth() {
        return (Sizes.getScreenSize(getContext()).x -
                Sizes.dpToPxExt((int) ((1 + customBannerPlace.bannersOnScreen()) * customBannerPlace.bannersGap() +
                        customBannerPlace.prevBannerOffset() + customBannerPlace.nextBannerOffset()
                ), getContext())) / (1f * customBannerPlace.bannersOnScreen());
    }

    @Override
    public void onUpdate(final BannerPagerState newValue) {
        if (newValue == null || newValue.loadState() == null) return;
        if (bannerPagerViewModel == null) return;
        Log.e("bannerPlace", "onUpdate " + bannerPagerViewModel);
        switch (newValue.loadState()) {
            case EMPTY:
                setVisibility(GONE);
            case FAILED:
            case NONE:
            case LOADING:
                //TODO ?
                break;
            case LOADED:
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        setVisibility(VISIBLE);
                        ViewGroup.LayoutParams layoutParams = bannerPager.getLayoutParams();
                        int height = calculateHeight(newValue.getItems());
                        layoutParams.height = height;
                        bannerPager.setClipToPadding(false);
                        bannerPager.setPadding(Sizes.dpToPxExt(customBannerPlace.prevBannerOffset() + customBannerPlace.bannersGap(), getContext()),
                                0,
                                Sizes.dpToPxExt(customBannerPlace.nextBannerOffset() + customBannerPlace.bannersGap(), getContext()),
                                0
                        );
                        bannerPager.setPageMargin(Sizes.dpToPxExt(customBannerPlace.bannersGap(), getContext()));
                        float iw = Sizes.getScreenSize(getContext()).x - Sizes.dpToPxExt(customBannerPlace.prevBannerOffset(), getContext()) - Sizes.dpToPxExt(customBannerPlace.nextBannerOffset(), getContext());
                        for (int i = 0; i < customBannerPlace.bannersOnScreen() - 1; i++) {
                            iw -= Sizes.dpToPxExt(customBannerPlace.bannersGap(), getContext());
                        }
                        float igap = Sizes.getScreenSize(getContext()).x - Sizes.dpToPxExt(customBannerPlace.prevBannerOffset(), getContext()) - Sizes.dpToPxExt(customBannerPlace.nextBannerOffset(), getContext());
                        bannerPager.requestLayout();
                        bannerPager.setAdapter(
                                new BannerPagerAdapter(
                                        core,
                                        newValue.getItems(),
                                        bannerPlace,
                                        false,
                                        (iw / igap) / customBannerPlace.bannersOnScreen(),
                                        height
                                )
                        );
                        bannerPager.setCurrentItem(0);
                    }
                });
                break;
        }
    }
}
