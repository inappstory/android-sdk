package com.inappstory.sdk.banners;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerPlaceState;
import com.inappstory.sdk.core.banners.IBannerPlaceViewModel;
import com.inappstory.sdk.core.banners.ICustomBannerPlace;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;

public class BannerList extends RelativeLayout implements Observer<BannerPlaceState> {
    private BannerPager bannerPager;
    private IBannerPlaceViewModel bannerPlaceViewModel;
    private String bannerPlace;
    private IASCore core;
    private ICustomBannerPlace customBannerPlace = new DefaultBannerPlace();
    private String lastLaunchedTag = "";

    public void bannerListLoadCallback(BannerListLoadCallback bannerListLoadCallback) {
        this.bannerListLoadCallback = bannerListLoadCallback != null ?
                bannerListLoadCallback : emptyBannerListLoadCallback;
    }

    private final BannerListLoadCallback emptyBannerListLoadCallback = new BannerListLoadCallback() {
        @Override
        public void bannerPlaceLoaded(int size, String bannerPlace, List<BannerData> bannerData) {

        }

        @Override
        public void loadError(String bannerPlace) {

        }

        @Override
        public void firstBannerLoaded(int bannerId, String bannerPlace) {

        }

        @Override
        public void firstBannerLoadError(int bannerId, String bannerPlace) {

        }
    };

    private BannerListLoadCallback bannerListLoadCallback = emptyBannerListLoadCallback;

    public void bannerListNavigationCallback(BannerListNavigationCallback bannerListNavigationCallback) {
        this.bannerListNavigationCallback = bannerListNavigationCallback != null ?
                bannerListNavigationCallback : emptyBannerListNavigationCallback;
    }

    private BannerListNavigationCallback emptyBannerListNavigationCallback = new BannerListNavigationCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }
    };


    private BannerListNavigationCallback bannerListNavigationCallback = emptyBannerListNavigationCallback;

    BannerPager.PageChangeListener pageChangeListener = new BannerPager.PageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            try {
                if (bannerListNavigationCallback != null)
                    bannerListNavigationCallback.onPageScrolled(
                            position,
                            positionOffset,
                            positionOffsetPixels
                    );
            } catch (Exception e) {
            }
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
            if (bannerPlaceViewModel != null) {
                bannerPlaceViewModel.updateCurrentIndex(position);
            }
            try {
                if (bannerListNavigationCallback != null)
                    bannerListNavigationCallback.onPageSelected(
                            position
                    );
            } catch (Exception e) {
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            String newLaunchedTag = "banner_" + bannerPager.getCurrentItem();
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (newLaunchedTag.equals(lastLaunchedTag)) {
                    BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
                    if (currentBannerView != null) currentBannerView.resumeBanner();
                }
            }
        }
    };

    public void showNext() {
        int currentItem = bannerPager.getCurrentItem();
        BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerPager.getAdapter();
        if (pagerAdapter != null) {
            if (currentItem + 1 >= pagerAdapter.getCount()) {
                if (pagerAdapter.isLoop()) {
                    bannerPager.setCurrentItem((currentItem + 1) % pagerAdapter.getDataCount(), true);
                } else {
                    //TODO log error
                }
            } else {
                bannerPager.setCurrentItem(currentItem + 1, true);
            }
        }
    }

    public void showPrev() {
        int currentItem = bannerPager.getCurrentItem();
        BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerPager.getAdapter();
        if (pagerAdapter != null) {
            if (currentItem - 1 < 0) {
                if (pagerAdapter.isLoop()) {
                    bannerPager.setCurrentItem((currentItem - 1) % pagerAdapter.getDataCount(), true);
                } else {
                    //TODO log error
                }
            } else {
                bannerPager.setCurrentItem(currentItem - 1, true);
            }
        }
    }

    public void showByIndex(int index) {
        BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerPager.getAdapter();
        if (pagerAdapter != null) {
            if (index < 0 || index >= pagerAdapter.getCount()) {
                if (pagerAdapter.isLoop()) {
                    bannerPager.setCurrentItem(index % pagerAdapter.getDataCount(), true);
                } else {
                    //TODO log error
                }
            } else {
                bannerPager.setCurrentItem(index, true);
            }
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_CANCEL && !lastLaunchedTag.isEmpty()) {
            BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
            if (currentBannerView != null) {
                currentBannerView.resumeBanner();
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setBannerPlace(final String bannerPlace) {
        this.bannerPlace = bannerPlace;
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerList.this.core = core;
                if (bannerPlaceViewModel != null)
                    bannerPlaceViewModel.removeSubscriber(BannerList.this);
                bannerPlaceViewModel = core
                        .widgetViewModels()
                        .bannerPlaceViewModels()
                        .get(bannerPlace);
                bannerPlaceViewModel.addSubscriber(BannerList.this);
                Log.e("bannerPlace", "setBannerPlace " + bannerPlaceViewModel.toString());
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        if (bannerPlaceViewModel != null)
            bannerPlaceViewModel.addSubscriber(BannerList.this);
        if (bannerPager.getAdapter() != null) {
            BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerPager.getAdapter();
            pagerAdapter.subscribeToFirst();
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (bannerPlaceViewModel != null)
            bannerPlaceViewModel.removeSubscriber(BannerList.this);
        if (bannerPager.getAdapter() != null) {
            BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerPager.getAdapter();
            pagerAdapter.unsubscribeFromFirst();
        }
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
        return Math.min((int) (realWidth / maxHeightRatio),
                Sizes.getScreenSize(getContext()).y);
    }

    private float calculateItemWidth() {
        return (Sizes.getScreenSize(getContext()).x -
                Sizes.dpToPxExt((int) ((1 + customBannerPlace.bannersOnScreen()) * customBannerPlace.bannersGap() +
                        customBannerPlace.prevBannerOffset() + customBannerPlace.nextBannerOffset()
                ), getContext())) / (1f * customBannerPlace.bannersOnScreen());
    }

    int lastIndex = -1;

    @Override
    public void onUpdate(final BannerPlaceState newValue) {
        if (newValue == null || newValue.loadState() == null) return;
        if (bannerPlaceViewModel == null) return;
        Log.e("bannerPlace", "onUpdate " + bannerPlaceViewModel);
        if (newValue.currentIndex() != null) {
            if (bannerPager.getCurrentItem() != newValue.currentIndex()) {
                bannerPager.post(new Runnable() {
                    @Override
                    public void run() {
                        bannerPager.setCurrentItem(newValue.currentIndex());
                    }
                });
            }
            return;
        }
        switch (newValue.loadState()) {
            case EMPTY:
                try {
                    if (bannerListLoadCallback != null)
                        bannerListLoadCallback.bannerPlaceLoaded(
                                0,
                                bannerPlace,
                                new ArrayList<BannerData>()
                        );
                } catch (Exception e) {
                }
                setVisibility(GONE);
                break;
            case FAILED:
                try {
                    if (bannerListLoadCallback != null)
                        bannerListLoadCallback.loadError(
                                bannerPlace
                        );
                } catch (Exception e) {
                }
                setVisibility(GONE);
                break;
            case NONE:
            case LOADING:
                //TODO ?
                break;
            case LOADED:
                try {
                    if (bannerListLoadCallback != null)
                        bannerListLoadCallback.bannerPlaceLoaded(
                                newValue.getItems().size(),
                                bannerPlace,
                                new ArrayList<BannerData>()
                        );
                } catch (Exception e) {
                }
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
                        BannerPagerAdapter adapter = new BannerPagerAdapter(
                                core,
                                newValue.getItems(),
                                bannerPlace,
                                new ICustomBannerPlaceholder() {
                                    @Override
                                    public View onCreate(Context context) {
                                        View v = customBannerPlace.loadingPlaceholder(context);
                                        if (v == null) {
                                            v = AppearanceManager.getLoader(context, Color.WHITE);
                                        }
                                        return v;
                                    }
                                },
                                bannerListLoadCallback,
                                newValue.iterationId(),
                                customBannerPlace.loop(),
                                (iw / igap) / customBannerPlace.bannersOnScreen(),
                                Sizes.dpToPxExt(
                                        customBannerPlace.cornerRadius(),
                                        getContext()
                                )
                        );
                        bannerPager.setAdapter(
                                adapter
                        );
                        int index = (newValue.currentIndex() == null) ?
                                adapter.getStartedIndex() :
                                newValue.currentIndex();
                        if (bannerPager.getCurrentItem() == index) {
                            pageChangeListener.onPageSelected(index);
                        } else {
                            bannerPager.setCurrentItem(index);
                        }

                    }
                });
                break;
        }
    }
}
