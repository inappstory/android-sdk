package com.inappstory.sdk.banners.ui.grid;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.banners.BannerData;
import com.inappstory.sdk.banners.BannerPlaceLoadCallback;
import com.inappstory.sdk.banners.IBannerHideCallback;
import com.inappstory.sdk.banners.UpdateBannerItemsData;
import com.inappstory.sdk.banners.UpdateBannerItemsType;
import com.inappstory.sdk.banners.ui.IBannersWidget;
import com.inappstory.sdk.banners.ui.banner.BannerView;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerDownloadManager;
import com.inappstory.sdk.core.banners.BannerListState;
import com.inappstory.sdk.core.banners.BannerListViewModel;
import com.inappstory.sdk.core.banners.BannerPlaceViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerWidgetViewModelType;
import com.inappstory.sdk.core.banners.BannersWidgetLoadStates;
import com.inappstory.sdk.core.banners.IBannerPlaceLoadCallback;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.banners.IBannersWidgetViewModel;
import com.inappstory.sdk.core.banners.ICustomBannerGridAppearance;
import com.inappstory.sdk.core.banners.InnerBannerPlaceLoadCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.utils.LoopedExecutor;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BannerGrid extends IasDynamicGridLayout implements Observer<BannerListState>, IBannersWidget {


    private String placeId;
    private IASCore core;
    private final String defaultUniqueId = UUID.randomUUID().toString();
    private String customUniqueId = null;
    private boolean initialized = false;
    private BannerListViewModel bannerGridViewModel;
    private BannersWidgetLoadStates currentLoadState = BannersWidgetLoadStates.EMPTY;

    private BannerPlaceLoadCallback bannerPlaceLoadCallback = null;

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

    @Override
    public void bannerHideCallback(IBannerHideCallback bannerHideCallback) {
        this.bannerHideCallback = bannerHideCallback;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bannerGridViewModel != null)
            bannerGridViewModel.singleTimeEvents().subscribeAndGetValue(steObserver);
        resumeBanners();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bannerGridViewModel != null)
            bannerGridViewModel.singleTimeEvents().unsubscribe(steObserver);
        pauseBanners();
    }

    private IBannerHideCallback bannerHideCallback = null;

    Observer<STETypeAndData> steObserver = new Observer<STETypeAndData>() {
        @Override
        public void onUpdate(STETypeAndData newValue) {
            if (newValue == null) return;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    switch (newValue.type()) {
                        case REMOVE_BANNER:
                            UpdateBannerItemsData data = (UpdateBannerItemsData) newValue.data();

                            if (data.type() == UpdateBannerItemsType.REMOVE) {
                                int bannerId = data.id();
                                IBanner removedData = null;
                                for (IBanner bannerData : data.items()) {
                                    if (bannerData.id() == bannerId) {
                                        removedData = bannerData;
                                        break;
                                    }
                                }
                                if (removedData != null) {
                                    removeItemAt(data.items().indexOf(removedData));
                                    if (bannerHideCallback != null)
                                        bannerHideCallback.bannerIsHidden(
                                                new BannerData(
                                                        removedData,
                                                        placeId
                                                ),
                                                -1
                                        );
                                }
                            }
                            break;
                    }
                }
            });

        }
    };

    private final IBannerPlaceLoadCallback internalBannerPlaceLoadCallback = new InnerBannerPlaceLoadCallback() {
        @Override
        public void bannerPlaceLoaded(List<IBanner> banners) {
            List<BannerData> bannerData = new ArrayList<>();
            if (bannerPlaceLoadCallback != null) {
                if (banners == null || banners.isEmpty()) {
                    bannerPlaceLoadCallback.bannerPlaceLoaded(0, new ArrayList<BannerData>(), WRAP_CONTENT);
                } else {
                    for (IBanner banner : banners) {
                        bannerData.add(
                                new BannerData(
                                        banner, placeId
                                )
                        );
                    }
                    bannerPlaceLoadCallback.bannerPlaceLoaded(
                            bannerData.size(),
                            bannerData,
                            -1
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
            if (bannerPlaceLoadCallback != null)
                bannerPlaceLoadCallback.bannerLoaded(bannerId, isCurrent);

        }

        @Override
        public void bannerLoadError(int bannerId, boolean isCurrent) {
            if (bannerPlaceLoadCallback != null)
                bannerPlaceLoadCallback.bannerLoadError(bannerId, isCurrent);
        }

        @Override
        public String bannerPlace() {
            return placeId;
        }
    };

    private void initVM() {
        if (initialized) return;
        if (bannerGridViewModel != null) {
            bannerGridViewModel.placeId(placeId);
            bannerGridViewModel.addSubscriberAndCheckLocal(BannerGrid.this);
            initialized = true;
        }
    }

    private void deInitVM() {
        initialized = false;
        if (bannerGridViewModel != null) {
            bannerGridViewModel.placeId(null);
            bannerGridViewModel.removeSubscriber(BannerGrid.this);
            bannerGridViewModel.clearBanners();
        }
        currentLoadState = null;
    }


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
        if (bannerGridViewModel != null)
            bannerGridViewModel.uniqueId(customUniqueId);
    }

    private boolean checkViewModelForSubscribers(String uniquePlaceId) {
        IASCore localCore = core;
        if (localCore == null)
            if (InAppStoryManager.getInstance() != null) {
                localCore = InAppStoryManager.getInstance().iasCore();
            } else {
                return true;
            }
        IBannersWidgetViewModel bannersWidgetViewModel = localCore
                .widgetViewModels()
                .bannerPlaceViewModels()
                .get(uniquePlaceId);
        return bannersWidgetViewModel != null && bannersWidgetViewModel.hasSubscribers(this);
    }

    private ICustomBannerGridAppearance customBannerGridAppearance =
            new DefaultBannerGridAppearance();

    public BannerGrid(Context context) {
        super(context);
        init(context);
    }

    public BannerGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerGrid(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {



        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerGrid.this.core = core;
                BannerPlaceViewModelsHolder holder = core
                        .widgetViewModels()
                        .bannerPlaceViewModels();
                bannerGridViewModel = (BannerListViewModel)
                        holder.getOrCreate(uniqueId(), BannerWidgetViewModelType.GRID);
            }
        });


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

    public void reloadBanners() {
        loadBanners(true);
    }

    public void loadBanners() {
        loadBanners(false);
    }

    public void loadBanners(boolean skipCache) {
        if (placeId == null || placeId.isEmpty()) {
            return;
        }
        if (bannerGridViewModel != null) {
            bannerGridViewModel.loadBanners(skipCache);
        }
    }

    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.customBannerGridAppearance = appearanceManager.csBannerGridInterface();
        settings(
                customBannerGridAppearance.align().value(),
                Sizes.dpToPxExt(customBannerGridAppearance.verticalGap(), getContext()),
                Sizes.dpToPxExt(customBannerGridAppearance.horizontalGap(), getContext()),
                customBannerGridAppearance.columnCount()
        );
    }

    private boolean checkVisibility() {
        Rect rowRect = new Rect();
        getGlobalVisibleRect(rowRect);
        Rect displayRect = new Rect();
        getWindowVisibleDisplayFrame(displayRect);
        return rowRect.top <= displayRect.bottom && rowRect.bottom >= displayRect.top;
    }

    private boolean externalPause = false;


    private void updateData() {
        BannerListState state = currentState.copy();
        List<IBanner> banners = state.getItems();
        int maxBannerWidth = getColumnWidth();
        setItems(
                banners,
                new ItemBinder<IBanner>() {
                    @Override
                    public View createView(ViewGroup parent, int position) {
                        IBanner banner = banners.get(position);

                        BannerView bannerView = new BannerView(parent.getContext());
                        IBannerViewModel bannerViewModel = bannerGridViewModel.getBannerViewModel(
                                banner.id(),
                                position
                        );
                        bannerViewModel.iterationId(state.iterationId());
                        String tag = "banner_" + position;
                        bannerView.setBannerBackground(banner.bannerAppearance().backgroundDrawable());
                        bannerView.setSize(-1, -1, true);
                        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                                maxBannerWidth,
                                (int) (maxBannerWidth / banner.bannerAppearance().singleBannerAspectRatio())
                        );
                        bannerView.setLayoutParams(lp);
                        bannerView.setTag(tag);
                        bannerView.viewModel(
                                bannerViewModel
                        );
                        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
                            @Override
                            public void use(@NonNull IASCore core) {
                                BannerDownloadManager bannerDownloadManager =
                                        core.contentLoader().bannerDownloadManager();
                                bannerDownloadManager.setMaxPriority(banner.id(), false);
                                bannerViewModel.loadContent(false, null);
                                bannerViewModel.bannerIsActive(true);
                            }
                        });
                        return bannerView;
                    }

                    @Override
                    public void bindView(View view, IBanner item, int position) {
                        IBanner banner = banners.get(position);
                    }
                }
        );
    }

    private LoopedExecutor executorService = null;

    private boolean paused = true;

    public void pauseBanners() {
        if (paused) return;
        paused = true;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((BannerView) getChildAt(i)).pauseBanner();
        }
        if (executorService != null) {
            executorService.cancelTask();
            executorService.shutdown();
            executorService = null;
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != VISIBLE)
            pauseBanners();
        else
            resumeBanners();
    }


    Runnable checkVisibilityRunnable = new Runnable() {
        @Override
        public void run() {
            int childCount = getChildCount();
            if (childCount == 0) {
                executorService.freeExecutor();
                return;
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                }
            });
            executorService.freeExecutor();
        }
    };

    public void resumeBanners() {
        if (!paused) return;
        paused = false;
        if (executorService == null) {
            executorService = new LoopedExecutor(1L, 300L, this.toString());
            executorService.task(checkVisibilityRunnable);
        }
        int childCount = getChildCount();

    }

    BannerListState currentState;

    @Override
    public void onUpdate(BannerListState newValue) {
        if (newValue == null || newValue.loadState() == null) return;
        if (bannerGridViewModel == null) return;
        currentLoadState = newValue.loadState();
        currentState = newValue;
        switch (newValue.loadState()) {
            case EMPTY:
            case LOADED:
                internalBannerPlaceLoadCallback.bannerPlaceLoaded(newValue.getItems());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        updateData();
                    }
                });
                break;
            case FAILED:
                internalBannerPlaceLoadCallback.loadError();
                break;
            case NONE:
            case LOADING:
                break;
        }
    }
}