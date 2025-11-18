package com.inappstory.sdk.banners.ui.list;


import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.banners.BannerPlaceLoadCallback;
import com.inappstory.sdk.banners.ICustomBannerPlaceholder;
import com.inappstory.sdk.banners.ui.IBannersWidget;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerListState;
import com.inappstory.sdk.core.banners.BannerListViewModel;
import com.inappstory.sdk.core.banners.BannerPlaceViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerWidgetViewModelType;
import com.inappstory.sdk.core.banners.BannersWidgetLoadStates;
import com.inappstory.sdk.core.banners.IBannerPlaceLoadCallback;
import com.inappstory.sdk.core.banners.IBannersWidgetViewModel;
import com.inappstory.sdk.core.banners.ICustomBannerListAppearance;
import com.inappstory.sdk.core.banners.InnerBannerPlaceLoadCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.banners.BannerData;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BannerList extends RecyclerView implements Observer<BannerListState>, IBannersWidget {

    private String placeId;
    private IASCore core;
    private final String defaultUniqueId = UUID.randomUUID().toString();
    private String customUniqueId = null;
    private boolean initialized = false;
    private BannerListViewModel bannerListViewModel;
    private BannersWidgetLoadStates currentLoadState = BannersWidgetLoadStates.EMPTY;

    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.customBannerListAppearance = appearanceManager.csBannerListInterface();
        updateAppearance();
    }

    private ICustomBannerListAppearance customBannerListAppearance =
            new DefaultBannerListAppearance();

    private LayoutManager defaultLayoutManager;

    public void customLayoutManager(LayoutManager customLayoutManager) {
        if (customLayoutManager != null) {
            this.customLayoutManager = customLayoutManager;
            setLayoutManager(this.customLayoutManager);
        }
    }

    private LayoutManager customLayoutManager;

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
        if (bannerListViewModel != null)
            bannerListViewModel.uniqueId(customUniqueId);
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

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        setLayoutManager(customLayoutManager != null ? customLayoutManager : defaultLayoutManager);
        updateAppearance();
        super.setAdapter(adapter);
    }


    private void updateAppearance() {
        final int cc = customBannerListAppearance.columnCount();
        final int orientation = customBannerListAppearance.orientation();
        if (cc > 1) {
            defaultLayoutManager = new GridLayoutManager(
                    getContext(),
                    cc,
                    orientation == HORIZONTAL ? HORIZONTAL : VERTICAL,
                    false
            );
        } else {
            defaultLayoutManager = new LinearLayoutManager(
                    getContext(),
                    orientation == HORIZONTAL ? HORIZONTAL : VERTICAL,
                    false
            );
        }

        setLayoutManager(customLayoutManager != null ? customLayoutManager : defaultLayoutManager);
        if (appearanceItemDecoration != null) {
            removeItemDecoration(appearanceItemDecoration);
        }
        appearanceItemDecoration = new ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                int itemCount = state.getItemCount();
                int edgeBannersPadding = Sizes.dpToPxExt(
                        customBannerListAppearance.edgeBannersPadding(),
                        getContext()
                );
                int bannersGap = Sizes.dpToPxExt(
                        customBannerListAppearance.bannersGap(),
                        getContext()
                );
                if (cc > 1) {
                    if (orientation == HORIZONTAL) {
                        outRect.right = 0;
                        outRect.left = bannersGap;
                        outRect.top = 0;
                        outRect.bottom = bannersGap;
                        if (position % cc == cc - 1) {
                            outRect.bottom = 0;
                        }
                        if (position / cc == 0) {
                            outRect.left = edgeBannersPadding;
                        }
                        int lastRow = (int) Math.ceil(1.0 * state.getItemCount() / cc);
                        if ((int) Math.ceil(1.0 * position / cc) == lastRow) {
                            outRect.right = edgeBannersPadding;
                        }
                    } else {
                        outRect.right = bannersGap;
                        outRect.left = 0;
                        outRect.top = bannersGap;
                        outRect.bottom = 0;
                        if (position % cc == 1) {
                            outRect.right = 0;
                        }
                        if (1 == 2) {
                            if (position % cc == 1) {
                                outRect.right = 0;
                            }
                            if (position / cc == 0) {
                                outRect.top = edgeBannersPadding;
                            }
                            int lastRow = (int) Math.ceil(1.0 * state.getItemCount() / cc);
                            if ((int) Math.ceil(1.0 * position / cc) == lastRow) {
                                outRect.bottom = edgeBannersPadding;
                            }
                        }
                    }
                } else {
                    if (orientation == HORIZONTAL) {
                        outRect.top = 0;
                        outRect.bottom = 0;
                        outRect.left = 0;
                        outRect.right = bannersGap;
                        if (position == 0) {
                            outRect.left = edgeBannersPadding;
                        } else if (position == itemCount - 1) {
                            outRect.right = edgeBannersPadding;
                        }
                    } else {
                        outRect.right = 0;
                        outRect.left = 0;
                        outRect.top = 0;
                        outRect.bottom = bannersGap;
                        if (position == 0) {
                            outRect.top = edgeBannersPadding;
                        } else if (position == itemCount - 1) {
                            outRect.bottom = edgeBannersPadding;
                        }
                    }
                }
            }
        };
        addItemDecoration(appearanceItemDecoration);
    }

    private ItemDecoration appearanceItemDecoration;

    private void init() {
        defaultLayoutManager = new LinearLayoutManager(
                getContext(),
                VERTICAL,
                false
        );
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerList.this.core = core;
                BannerPlaceViewModelsHolder holder = core
                        .widgetViewModels()
                        .bannerPlaceViewModels();
                bannerListViewModel = (BannerListViewModel)
                        holder.getOrCreate(uniqueId(), BannerWidgetViewModelType.LAZY_LIST);
            }
        });
    }

    public BannerList(
            @NonNull Context context
    ) {
        super(context);
        init();
    }

    public BannerList(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public BannerList(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
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

    public void reloadBanners() {
        loadBanners(true);
    }

    public void loadBanners() {
        loadBanners(false);
    }

    public void loadBanners(boolean skipCache) {
        if (placeId == null || placeId.isEmpty()) {
            //TODO Log error
            return;
        }
        if (bannerListViewModel != null) {
            bannerListViewModel.loadBanners(skipCache);
        }
    }


    private void initAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerPlace);
        setPlaceId(typedArray.getString(R.styleable.BannerPlace_cs_place_id));
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

    private void initVM() {
        if (initialized) return;
        if (bannerListViewModel != null) {
            bannerListViewModel.placeId(placeId);
            bannerListViewModel.addSubscriberAndCheckLocal(BannerList.this);
            initialized = true;
        }
    }

    private void deInitVM() {
        initialized = false;
        if (bannerListViewModel != null) {
            bannerListViewModel.placeId(null);
            bannerListViewModel.removeSubscriber(BannerList.this);
            bannerListViewModel.clearBanners();
        }
        currentLoadState = null;
        //TODO clear list
    }

    private int getItemWidth() {
        int cc = customBannerListAppearance.columnCount();
        return (getWidth() -
                (cc - 1) * Sizes.dpToPxExt(customBannerListAppearance.bannersGap(),
                        getContext())) / cc;
    }

    @Override
    public void onUpdate(BannerListState newValue) {
        if (newValue == null || newValue.loadState() == null) return;
        if (bannerListViewModel == null) return;
        currentLoadState = newValue.loadState();
        switch (newValue.loadState()) {
            case EMPTY:
                internalBannerPlaceLoadCallback.bannerPlaceLoaded(new ArrayList<IBanner>());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = 0;
                        setLayoutManager(customLayoutManager != null ?
                                customLayoutManager : defaultLayoutManager
                        );
                        BannerListAdapter adapter = new BannerListAdapter(core,
                                new ArrayList<IBanner>(),
                                placeId,
                                uniqueId(),
                                bannerPlaceLoadCallback,
                                new ICustomBannerPlaceholder() {
                                    @Override
                                    public View onCreate(Context context) {
                                        View v = customBannerListAppearance.loadingPlaceholder(context);
                                        if (v == null) {
                                            v = AppearanceManager.getLoader(context, Color.WHITE);
                                        }
                                        return v;
                                    }
                                },
                                newValue.iterationId(),
                                getItemWidth(),
                                Sizes.dpToPxExt(
                                        16,
                                        getContext()
                                )
                        );
                        setAdapter(null);
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
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        //layoutParams.height = 0;
                        setLayoutManager(
                                customLayoutManager != null ?
                                        customLayoutManager : defaultLayoutManager
                        );
                        List<IBanner> items = newValue.getItems();
                        BannerListAdapter adapter = new BannerListAdapter(
                                core,
                                items,
                                placeId,
                                uniqueId(),
                                bannerPlaceLoadCallback,
                                new ICustomBannerPlaceholder() {
                                    @Override
                                    public View onCreate(Context context) {
                                        View v = customBannerListAppearance.loadingPlaceholder(context);
                                        if (v == null) {
                                            v = AppearanceManager.getLoader(context, Color.WHITE);
                                        }
                                        return v;
                                    }
                                },
                                newValue.iterationId(),
                                getItemWidth(),
                                Sizes.dpToPxExt(
                                        16,
                                        getContext()
                                )
                        );
                        setAdapter(adapter);
                    }
                });
                break;
        }
    }
}
