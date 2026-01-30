package com.inappstory.sdk.banners.ui.carousel;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
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
import com.inappstory.sdk.banners.BannerCarouselNavigationCallback;
import com.inappstory.sdk.banners.BannerPlaceLoadCallback;
import com.inappstory.sdk.banners.ui.IBannersWidget;
import com.inappstory.sdk.banners.ui.banner.BannerView;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.banners.BannerCarouselViewModel;
import com.inappstory.sdk.core.banners.BannerPlaceViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerWidgetViewModelType;
import com.inappstory.sdk.core.banners.IBannerPlaceLoadCallback;
import com.inappstory.sdk.banners.ICustomBannerPlaceholder;
import com.inappstory.sdk.core.banners.InnerBannerPlaceLoadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannersWidgetLoadStates;
import com.inappstory.sdk.core.banners.BannerCarouselState;
import com.inappstory.sdk.core.banners.IBannersWidgetViewModel;
import com.inappstory.sdk.core.banners.ICustomBannerCarouselAppearance;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.banners.BannerData;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BannerCarousel extends FrameLayout implements Observer<BannerCarouselState>, IBannersWidget {
    private BannerViewPager bannerViewPager;
    private BannerCarouselViewModel bannerCarouselViewModel;
    private String placeId;
    private IASCore core;
    private ICustomBannerCarouselAppearance customBannerPlaceAppearance = new DefaultBannerCarouselAppearance();
    private String lastLaunchedTag = "";
    private final String defaultUniqueId = UUID.randomUUID().toString();
    private String customUniqueId = null;
    private boolean scrollManage = false;
    private boolean initialized = false;
    private final Object scrollManageLock = new Object();
    private BannersWidgetLoadStates currentLoadState = BannersWidgetLoadStates.EMPTY;


    public String uniqueId() {
        return customUniqueId != null ? customUniqueId : defaultUniqueId;
    }

    public void uniqueId(String customUniqueId) {
        if (customUniqueId == null || customUniqueId.isEmpty()) {
            //TODO Log error
            return;
        }
        if (Objects.equals(this.customUniqueId, customUniqueId)) return;
        if (checkViewModelForSubscribers(customUniqueId)) {
            //TODO Log error
            return;
        }
        this.customUniqueId = customUniqueId;
        if (bannerCarouselViewModel != null)
            bannerCarouselViewModel.uniqueId(customUniqueId);
    }

    public void reloadBanners() {
        loadBanners(true);
    }

    public void loadBanners() {
        loadBanners(false);
    }

    public void removeItemByIndex(int index) {
        bannerCarouselViewModel.removeBanner(index);
        ((BannerPagerAdapter)bannerViewPager.getAdapter()).removeBanner(index);
    }

    public void loadCallback(BannerPlaceLoadCallback bannerPlaceLoadCallback) {
        if (bannerPlaceLoadCallback.bannerPlace() == null) {
            if (placeId != null) {
                bannerPlaceLoadCallback.bannerPlace(placeId);
            } else {
                //TODO Log error
            }
        }
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
                        bannerData.add(new BannerData(banner.id(), placeId, banner.slideEventPayload(0)));
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
        public String bannerPlace() {
            return placeId;
        }
    };

    private BannerPlaceLoadCallback bannerPlaceLoadCallback = null;

    public void navigationCallback(BannerCarouselNavigationCallback bannerCarouselNavigationCallback) {
        this.bannerCarouselNavigationCallback = bannerCarouselNavigationCallback != null ?
                bannerCarouselNavigationCallback : emptyBannerCarouselNavigationCallback;
    }

    private BannerCarouselNavigationCallback emptyBannerCarouselNavigationCallback = new BannerCarouselNavigationCallback() {

        @Override
        public void onPageScrolled(int position, int total, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position, int total) {

        }
    };


    private BannerCarouselNavigationCallback bannerCarouselNavigationCallback = emptyBannerCarouselNavigationCallback;

    BannerViewPager.PageChangeListener pageChangeListener = new BannerViewPager.PageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (bannerCarouselViewModel != null) {
                BannerCarouselState placeState = bannerCarouselViewModel.getCurrentBannerPlaceState();
                try {
                    if (bannerCarouselNavigationCallback != null && !placeState.getItems().isEmpty()) {
                        int size = placeState.getItems().size();
                        int pos = ((position % size) + size) % size;
                        bannerCarouselNavigationCallback.onPageScrolled(
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
                BannerView currentBannerView = bannerViewPager.findViewWithTag(lastLaunchedTag);
                if (currentBannerView != null) currentBannerView.stopBanner();
            }
            Log.e("SlideLC", "onPageSelected " + position);
            BannerView currentBannerView = bannerViewPager.findViewWithTag(newLaunchedTag);
            if (currentBannerView != null) {
                currentBannerView.startBanner();
                currentBannerView.resumeBanner();
            }
            lastLaunchedTag = newLaunchedTag;
            if (bannerCarouselViewModel != null) {
                bannerCarouselViewModel.updateCurrentIndex(position);
                BannerCarouselState placeState = bannerCarouselViewModel.getCurrentBannerPlaceState();
                try {
                    if (bannerCarouselNavigationCallback != null && !placeState.getItems().isEmpty()) {
                        int size = placeState.getItems().size();
                        int pos = ((position % size) + size) % size;
                        bannerCarouselNavigationCallback.onPageSelected(
                                pos, size
                        );
                    }
                } catch (Exception e) {
                }
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            String newLaunchedTag = "banner_" + bannerViewPager.getCurrentItem();
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                synchronized (scrollManageLock) {
                    scrollManage = false;
                }
                if (newLaunchedTag.equals(lastLaunchedTag)) {
                    BannerView currentBannerView = bannerViewPager.findViewWithTag(lastLaunchedTag);
                    if (currentBannerView != null) currentBannerView.resumeBanner();
                }
            } else {
                synchronized (scrollManageLock) {
                    scrollManage = true;
                }
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {

                    if (newLaunchedTag.equals(lastLaunchedTag)) {
                        BannerView currentBannerView = bannerViewPager.findViewWithTag(lastLaunchedTag);
                        if (currentBannerView != null) currentBannerView.pauseBanner();
                    }
                }
            }
        }
    };

    public void showNext() {
        int currentItem = bannerViewPager.getCurrentItem();
        BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerViewPager.getAdapter();
        if (pagerAdapter != null) {
            if (currentItem + 1 >= pagerAdapter.getCount()) {
                if (pagerAdapter.isLoop()) {
                    bannerViewPager.setCurrentItem((currentItem + 1) % pagerAdapter.getDataCount(), true);
                } else {
                    //TODO log error
                }
            } else {
                bannerViewPager.setCurrentItem(currentItem + 1, true);
            }
        }
    }

    public void resumeAutoscroll() {
        synchronized (scrollManageLock) {
            if (scrollManage) return;
        }
        BannerView currentBannerView = bannerViewPager.findViewWithTag(lastLaunchedTag);
        if (currentBannerView != null) {
            currentBannerView.resumeBanner();
        }
    }

    public void pauseAutoscroll() {
        synchronized (scrollManageLock) {
            if (scrollManage) return;
        }
        BannerView currentBannerView = bannerViewPager.findViewWithTag(lastLaunchedTag);
        if (currentBannerView != null) {
            currentBannerView.pauseBanner();
        }
    }

    public void showPrevious() {
        int currentItem = bannerViewPager.getCurrentItem();
        BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerViewPager.getAdapter();
        if (pagerAdapter != null) {
            if (currentItem - 1 < 0) {
                if (pagerAdapter.isLoop()) {
                    bannerViewPager.setCurrentItem((currentItem - 1) % pagerAdapter.getDataCount(), true);
                } else {
                    //TODO log error
                }
            } else {
                bannerViewPager.setCurrentItem(currentItem - 1, true);
            }
        }
    }

    public void showByIndex(int index) {
        BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerViewPager.getAdapter();
        if (pagerAdapter != null) {
            if (index < 0 || index >= pagerAdapter.getDataCount()) {
                //TODO log error
                return;
            }
            int currentItem = bannerViewPager.getCurrentItem();
            int dataCount = pagerAdapter.getDataCount();
            int zeroItem = currentItem - (currentItem % pagerAdapter.getDataCount());
            int indexItem = zeroItem + ((index % dataCount + dataCount) % dataCount);
            if (indexItem == currentItem) return;
            bannerViewPager.setCurrentItem(indexItem, false);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_CANCEL && !lastLaunchedTag.isEmpty()) {
            BannerView currentBannerView = bannerViewPager.findViewWithTag(lastLaunchedTag);
            if (currentBannerView != null) {
                currentBannerView.resumeBanner();
            }
        }
        return super.onInterceptTouchEvent(event);
    }


    public void loadBanners(boolean skipCache) {
        if (placeId == null || placeId.isEmpty()) {
            //TODO Log error
            return;
        }
        if (bannerCarouselViewModel != null) {
            bannerCarouselViewModel.loadBanners(skipCache);
        }
    }

    public void clear() {
        bannerCarouselViewModel.clear();
    }

    private void initVM() {
        if (initialized) return;
        if (bannerCarouselViewModel != null) {
            bannerCarouselViewModel.placeId(placeId);
            bannerCarouselViewModel.addSubscriberAndCheckLocal(BannerCarousel.this);
            initialized = true;
        }
    }

    private void deInitVM() {
        initialized = false;
        if (bannerCarouselViewModel != null) {
            bannerCarouselViewModel.placeId(null);
            bannerCarouselViewModel.removeSubscriber(BannerCarousel.this);
            bannerCarouselViewModel.clearBanners();
        }
        currentLoadState = null;
        bannerViewPager.setSaveFromParentEnabled(false);
        if (bannerViewPager.getAdapter() != null) {
            BannerPagerAdapter pagerAdapter = (BannerPagerAdapter) bannerViewPager.getAdapter();
           // pagerAdapter.unsubscribeFromFirst();
            float iw = Sizes.getScreenSize(getContext()).x -
                    Sizes.dpToPxExt(customBannerPlaceAppearance.prevBannerOffset(), getContext())
                    - Sizes.dpToPxExt(customBannerPlaceAppearance.nextBannerOffset(), getContext());
            for (int i = 0; i < customBannerPlaceAppearance.bannersOnScreen() - 1; i++) {
                iw -= Sizes.dpToPxExt(customBannerPlaceAppearance.bannersGap(), getContext());
            }
            BannerPagerAdapter adapter = new BannerPagerAdapter(
                    core,
                    new ArrayList<IBanner>(),
                    null,
                    null,
                    null,
                    null,
                    "",
                    false,
                    -1,
                    -1,
                    -1
            );
            ViewGroup.LayoutParams layoutParams = bannerViewPager.getLayoutParams();
            layoutParams.height = WRAP_CONTENT;
            Log.e("BannerProfiling", "setAdapter deInit" + placeId);
            bannerViewPager.setAdapter(adapter);
            pagerAdapter.clear();
        }
    }

    private boolean checkViewModelForSubscribers(String uniquePlaceId) {
        IASCore localCore = core;
        if (localCore == null)
            if (InAppStoryManager.getInstance() != null) {
                localCore = InAppStoryManager.getInstance().iasCore();
            } else {
                return true;
            }
        IBannersWidgetViewModel bannerPlaceViewModel = localCore
                .widgetViewModels()
                .bannerPlaceViewModels()
                .get(uniquePlaceId);
        return bannerPlaceViewModel != null && bannerPlaceViewModel.hasSubscribers(this);
    }

    public void setPlaceId(final String placeId) {
        if (Objects.equals(this.placeId, placeId)) return;
        this.placeId = placeId;
        if (placeId == null || placeId.isEmpty()) {
            deInitVM();
        } else {
            initVM();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resumeAutoscroll();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        BannerView currentBannerView = bannerViewPager.findViewWithTag(lastLaunchedTag);
        if (currentBannerView != null) {
            if (hasWindowFocus)
                currentBannerView.resumeBanner();
            else
                currentBannerView.pauseBanner();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // deInit();
        super.onDetachedFromWindow();
        pauseAutoscroll();
    }

    public BannerCarousel(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BannerCarousel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(context, attrs);

    }

    private void initAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerPlace);
        setPlaceId(typedArray.getString(R.styleable.BannerPlace_cs_place_id));
    }

    public BannerCarousel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(context, attrs);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.cs_banner_widget, this);
        bannerViewPager = findViewById(R.id.banner_pager);
        bannerViewPager.addOnPageChangeListener(pageChangeListener);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerCarousel.this.core = core;
                BannerPlaceViewModelsHolder holder = core
                        .widgetViewModels()
                        .bannerPlaceViewModels();
                bannerCarouselViewModel =
                        (BannerCarouselViewModel) holder.getOrCreate(
                                uniqueId(),
                                BannerWidgetViewModelType.PAGER
                        );
            }
        });
    }

    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.customBannerPlaceAppearance = appearanceManager.csBannerPlaceInterface();
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
                Sizes.dpToPxExt((int) ((1 + customBannerPlaceAppearance.bannersOnScreen()) * customBannerPlaceAppearance.bannersGap() +
                        customBannerPlaceAppearance.prevBannerOffset() + customBannerPlaceAppearance.nextBannerOffset()
                ), getContext())) / (1f * customBannerPlaceAppearance.bannersOnScreen());
    }


    @Override
    public void onUpdate(final BannerCarouselState newValue) {
        if (newValue == null || newValue.loadState() == null) return;
        if (bannerCarouselViewModel == null) return;
        if (currentLoadState == BannersWidgetLoadStates.LOADED && newValue.currentIndex() != null) {
            if (bannerViewPager.getCurrentItem() != newValue.currentIndex()) {
                Log.e("Indexes", bannerViewPager.getCurrentItem() + " " + newValue.currentIndex());
                bannerViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        bannerViewPager.setCurrentItem(newValue.currentIndex(), true);
                        bannerCarouselViewModel.updateCurrentIndex(newValue.currentIndex());
                    }
                });
            }
            return;
        }
        currentLoadState = newValue.loadState();
        switch (newValue.loadState()) {
            case EMPTY:
                internalBannerPlaceLoadCallback.bannerPlaceLoaded(new ArrayList<IBanner>());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = bannerViewPager.getLayoutParams();
                        layoutParams.height = 0;
                        BannerPagerAdapter adapter = new BannerPagerAdapter(
                                core,
                                new ArrayList<IBanner>(),
                                placeId,
                                uniqueId(),
                                null,
                                bannerPlaceLoadCallback,
                                newValue.iterationId(),
                                false,
                                0,
                                calculateItemWidth(),
                                Sizes.dpToPxExt(
                                        customBannerPlaceAppearance.cornerRadius(),
                                        getContext()
                                )
                        );
                        bannerViewPager.setOffscreenPageLimit(1);
                        Log.e("BannerProfiling", "setAdapter empty" + placeId);
                        IASDataSettingsHolder dataSettingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
                        if (dataSettingsHolder.changeLayoutDirection()) {
                            Configuration configuration = new Configuration();
                            configuration.setLocale(dataSettingsHolder.lang());
                            setLayoutDirection(configuration.getLayoutDirection());
                        }
                        bannerViewPager.setAdapter(
                                adapter
                        );
                    }
                });
                break;
            case FAILED:
                internalBannerPlaceLoadCallback.loadError();
                break;
            case NONE:
            case LOADING:
                break;
            case LOADED:
                internalBannerPlaceLoadCallback.bannerPlaceLoaded(newValue.getItems());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = bannerViewPager.getLayoutParams();
                        int height = calculateHeight(newValue.getItems());
                        float itemWidth = calculateItemWidth();
                        layoutParams.height = height;
                        bannerViewPager.setClipToPadding(false);
                        bannerViewPager.setPadding(Sizes.dpToPxExt(customBannerPlaceAppearance.prevBannerOffset() + customBannerPlaceAppearance.bannersGap(), getContext()),
                                0,
                                Sizes.dpToPxExt(customBannerPlaceAppearance.nextBannerOffset() + customBannerPlaceAppearance.bannersGap(), getContext()),
                                0
                        );
                        bannerViewPager.setPageMargin(Sizes.dpToPxExt(customBannerPlaceAppearance.bannersGap(), getContext()));
                        float iw = Sizes.getScreenSize(getContext()).x - Sizes.dpToPxExt(customBannerPlaceAppearance.prevBannerOffset(), getContext()) - Sizes.dpToPxExt(customBannerPlaceAppearance.nextBannerOffset(), getContext());
                        for (int i = 0; i < customBannerPlaceAppearance.bannersOnScreen() - 1; i++) {
                            iw -= Sizes.dpToPxExt(customBannerPlaceAppearance.bannersGap(), getContext());
                        }
                        float igap = Sizes.getScreenSize(getContext()).x - Sizes.dpToPxExt(customBannerPlaceAppearance.prevBannerOffset(), getContext()) - Sizes.dpToPxExt(customBannerPlaceAppearance.nextBannerOffset(), getContext());
                        List<IBanner> items = newValue.getItems();
                        BannerPagerAdapter adapter = new BannerPagerAdapter(
                                core,
                                items,
                                placeId,
                                uniqueId(),
                                new ICustomBannerPlaceholder() {
                                    @Override
                                    public View onCreate(Context context) {
                                        View v = customBannerPlaceAppearance.loadingPlaceholder(context);
                                        if (v == null) {
                                            v = AppearanceManager.getLoader(context, Color.WHITE);
                                        }
                                        return v;
                                    }
                                },
                                bannerPlaceLoadCallback,
                                newValue.iterationId(),
                                customBannerPlaceAppearance.loop() &&
                                        (items.size() >= customBannerPlaceAppearance.bannersOnScreen() + 1),
                                (iw / igap) / customBannerPlaceAppearance.bannersOnScreen(),
                                itemWidth,
                                Sizes.dpToPxExt(
                                        customBannerPlaceAppearance.cornerRadius(),
                                        getContext()
                                )
                        );
                        bannerViewPager.setOffscreenPageLimit(1);
                        Log.e("BannerProfiling", "setAdapter values" + placeId);
                        IASDataSettingsHolder dataSettingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
                        if (dataSettingsHolder.changeLayoutDirection()) {
                            Configuration configuration = new Configuration();
                            configuration.setLocale(dataSettingsHolder.lang());
                            setLayoutDirection(configuration.getLayoutDirection());
                        }
                        bannerViewPager.setAdapter(
                                adapter
                        );
                        int index = (newValue.currentIndex() == null) ?
                                adapter.getStartedIndex() :
                                newValue.currentIndex();
                        if (bannerViewPager.getCurrentItem() == index) {
                            pageChangeListener.onPageSelected(index);
                        } else {
                            bannerViewPager.setCurrentItem(index, true);
                        }

                    }
                });
                break;
        }
    }
}
