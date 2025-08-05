package com.inappstory.sdk.banners;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerPlaceLoadStates;
import com.inappstory.sdk.core.banners.BannerPlaceState;
import com.inappstory.sdk.core.banners.BannerPlaceViewModel;
import com.inappstory.sdk.core.banners.IBannerPlaceViewModel;
import com.inappstory.sdk.core.banners.ICustomBannerPlaceAppearance;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BannerPlace extends FrameLayout implements Observer<BannerPlaceState> {
    private BannerPager bannerPager;
    private IBannerPlaceViewModel bannerPlaceViewModel;
    private String bannerPlace;
    private IASCore core;
    private ICustomBannerPlaceAppearance customBannerPlace = new DefaultBannerPlaceAppearance();
    private String lastLaunchedTag = "";

   /* private void loadBanners() {
        final String localBannerPlace = bannerPlace;
        if (localBannerPlace == null) {
            //TODO Log error
            return;
        }
        if (bannerPlaceViewModel != null) {
            bannerPlaceViewModel.clear();
        }
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.bannersAPI().loadBannerPlace(localBannerPlace);
            }
        });
    }*/

    public void setLoadCallback(BannerPlaceLoadCallback bannerPlaceLoadCallback) {
        this.bannerPlaceLoadCallback = bannerPlaceLoadCallback;
    }


    final IBannerPlaceLoadCallback internalBannerPlaceLoadCallback = new InnerBannerPlaceLoadCallback() {
        @Override
        public void bannerPlaceLoaded(List<IBanner> banners) {
            List<BannerData> bannerData = new ArrayList<>();
            if (bannerPlaceLoadCallback != null) {
                if (banners == null || banners.isEmpty()) {
                    bannerPlaceLoadCallback.bannerPlaceLoaded(0, new ArrayList<BannerData>(), WRAP_CONTENT);
                } else {
                    for (IBanner banner : banners) {
                        bannerData.add(new BannerData(banner.id(), bannerPlace));
                    }
                    bannerPlaceLoadCallback.bannerPlaceLoaded(
                            bannerData.size(),
                            bannerData,
                            calculateHeight(banners)
                    );
                }
            }
        }

        @Override
        public void loadError() {
            if (bannerPlaceLoadCallback != null) bannerPlaceLoadCallback.loadError();

        }

        @Override
        public void bannerLoaded(int bannerId, boolean isCurrent) {
            if (bannerPlaceLoadCallback != null) bannerLoaded(bannerId, isCurrent);

        }

        @Override
        public void bannerLoadError(int bannerId, boolean isCurrent) {
            if (bannerPlaceLoadCallback != null) bannerLoadError(bannerId, isCurrent);
        }

        @Override
        String bannerPlace() {
            return bannerPlace;
        }
    };

    private BannerPlaceLoadCallback bannerPlaceLoadCallback = null;

    public void bannerListNavigationCallback(BannerListNavigationCallback bannerListNavigationCallback) {
        this.bannerListNavigationCallback = bannerListNavigationCallback != null ?
                bannerListNavigationCallback : emptyBannerListNavigationCallback;
    }

    private BannerListNavigationCallback emptyBannerListNavigationCallback = new BannerListNavigationCallback() {

        @Override
        public void onPageScrolled(int position, int total, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position, int total) {

        }
    };


    private boolean scrollManage = false;
    private final Object scrollManageLock = new Object();

    private BannerListNavigationCallback bannerListNavigationCallback = emptyBannerListNavigationCallback;

    BannerPager.PageChangeListener pageChangeListener = new BannerPager.PageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (bannerPlaceViewModel != null) {
                BannerPlaceState placeState = bannerPlaceViewModel.getCurrentBannerPagerState();
                try {
                    if (bannerListNavigationCallback != null && !placeState.getItems().isEmpty()) {
                        int size = placeState.getItems().size();
                        int pos = ((position % size) + size) % size;
                        bannerListNavigationCallback.onPageScrolled(
                                pos,
                                size,
                                positionOffset,
                                positionOffsetPixels
                        );
                    }
                } catch (Exception e) {
                }
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
                BannerPlaceState placeState = bannerPlaceViewModel.getCurrentBannerPagerState();
                try {
                    if (bannerListNavigationCallback != null && !placeState.getItems().isEmpty()) {
                        int size = placeState.getItems().size();
                        int pos = ((position % size) + size) % size;
                        bannerListNavigationCallback.onPageSelected(
                                pos, size
                        );
                    }
                } catch (Exception e) {
                }
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            String newLaunchedTag = "banner_" + bannerPager.getCurrentItem();
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                synchronized (scrollManageLock) {
                    scrollManage = false;
                }
                if (newLaunchedTag.equals(lastLaunchedTag)) {
                    BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
                    if (currentBannerView != null) currentBannerView.resumeBanner();
                }
            } else {
                synchronized (scrollManageLock) {
                    scrollManage = true;
                }
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {

                    if (newLaunchedTag.equals(lastLaunchedTag)) {
                        BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
                        if (currentBannerView != null) currentBannerView.pauseBanner();
                    }
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

    public void resumeAutoscroll() {
        synchronized (scrollManageLock) {
            if (scrollManage) return;
        }
        BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
        if (currentBannerView != null) {
            currentBannerView.resumeBanner();
        }
    }

    public void pauseAutoscroll() {
        synchronized (scrollManageLock) {
            if (scrollManage) return;
        }
        BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
        if (currentBannerView != null) {
            currentBannerView.pauseBanner();
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
            if (index < 0 || index >= pagerAdapter.getDataCount()) {
                //TODO log error
                return;
            }
            int currentItem = bannerPager.getCurrentItem();
            int dataCount = pagerAdapter.getDataCount();
            int zeroItem = currentItem - (currentItem % pagerAdapter.getDataCount());
            int indexItem = zeroItem + ((index % dataCount + dataCount) % dataCount);
            if (indexItem == currentItem) return;
            bannerPager.setCurrentItem(indexItem, false);
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

    private boolean initialized = false;

    private void init() {
        if (initialized) return;
        initialized = true;
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerPlace.this.core = core;
                bannerPlaceViewModel = core
                        .widgetViewModels()
                        .bannerPlaceViewModels()
                        .get(bannerPlace);
                bannerPlaceViewModel.addBannerPlaceLoadCallback((InnerBannerPlaceLoadCallback) internalBannerPlaceLoadCallback);
                bannerPlaceViewModel.addSubscriberAndCheckLocal(BannerPlace.this);
            }
        });
    }

    private void deInit() {
        initialized = false;
        if (bannerPlaceViewModel != null) {
            bannerPlaceViewModel.removeSubscriber(BannerPlace.this);
            bannerPlaceViewModel.removeBannerPlaceLoadCallback((InnerBannerPlaceLoadCallback) internalBannerPlaceLoadCallback);
            bannerPlaceViewModel.clearBanners();
            bannerPlaceViewModel.clear();
            bannerPlaceViewModel = null;
        }
        currentLoadState = null;
        bannerPager.setSaveFromParentEnabled(false);
        if (bannerPager.getAdapter() != null) {
            BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerPager.getAdapter();
            pagerAdapter.unsubscribeFromFirst();
            float iw = Sizes.getScreenSize(getContext()).x -
                    Sizes.dpToPxExt(customBannerPlace.prevBannerOffset(), getContext())
                    - Sizes.dpToPxExt(customBannerPlace.nextBannerOffset(), getContext());
            for (int i = 0; i < customBannerPlace.bannersOnScreen() - 1; i++) {
                iw -= Sizes.dpToPxExt(customBannerPlace.bannersGap(), getContext());
            }
            BannerPagerAdapter adapter = new BannerPagerAdapter(
                    core,
                    new ArrayList<IBanner>(),
                    null,
                    null,
                    null,
                    "",
                    false,
                    -1,
                    -1
            );
            ViewGroup.LayoutParams layoutParams = bannerPager.getLayoutParams();
            layoutParams.height = WRAP_CONTENT;
            bannerPager.setAdapter(adapter);
            pagerAdapter.clear();
        }
    }

    private boolean checkViewModelForSubscribers(String bannerPlace) {
        IBannerPlaceViewModel bannerPlaceViewModel = core
                .widgetViewModels()
                .bannerPlaceViewModels()
                .get(bannerPlace);
        return bannerPlaceViewModel.hasSubscribers(this);
    }

    public void setBannerPlace(final String bannerPlace) {
        if (Objects.equals(this.bannerPlace, bannerPlace)) return;
        if (checkViewModelForSubscribers(bannerPlace)) {
            //TODO Log error
            return;
        }
        this.bannerPlace = bannerPlace;
        if (bannerPlace != null && !bannerPlace.isEmpty()) {
            init();
        } else {
            deInit();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bannerPlaceViewModel != null && bannerPlace != null && !bannerPlace.isEmpty()) {
            if (checkViewModelForSubscribers(bannerPlace))
            init();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        BannerView currentBannerView = bannerPager.findViewWithTag(lastLaunchedTag);
        if (currentBannerView != null) {
            if (hasWindowFocus)
                currentBannerView.resumeBanner();
            else
                currentBannerView.pauseBanner();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        deInit();
        super.onDetachedFromWindow();
    }

    public BannerPlace(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BannerPlace(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerPlace(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.cs_banner_widget, this);
        bannerPager = findViewById(R.id.banner_pager);
        bannerPager.addOnPageChangeListener(pageChangeListener);
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

    private BannerPlaceLoadStates currentLoadState = BannerPlaceLoadStates.EMPTY;

    @Override
    public void onUpdate(final BannerPlaceState newValue) {
        if (newValue == null || newValue.loadState() == null) return;
        if (bannerPlaceViewModel == null) return;
        if (currentLoadState == BannerPlaceLoadStates.LOADED && newValue.currentIndex() != null) {
            if (bannerPager.getCurrentItem() != newValue.currentIndex()) {
                bannerPager.post(new Runnable() {
                    @Override
                    public void run() {
                        bannerPager.setCurrentItem(newValue.currentIndex());
                        bannerPlaceViewModel.updateCurrentIndex(newValue.currentIndex());
                    }
                });
            }
            return;
        }
        currentLoadState = newValue.loadState();
        switch (newValue.loadState()) {
            case EMPTY:
            case FAILED:
            case NONE:
            case LOADING:
                break;
            case LOADED:
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
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
                        List<IBanner> items = newValue.getItems();
                        BannerPagerAdapter adapter = new BannerPagerAdapter(
                                core,
                                items,
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
                                bannerPlaceLoadCallback,
                                newValue.iterationId(),
                                customBannerPlace.loop() &&
                                        (items.size() >= customBannerPlace.bannersOnScreen() + 1),
                                (iw / igap) / customBannerPlace.bannersOnScreen(),
                                Sizes.dpToPxExt(
                                        customBannerPlace.cornerRadius(),
                                        getContext()
                                )
                        );
                        bannerPager.setOffscreenPageLimit(1);
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
