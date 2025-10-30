package com.inappstory.sdk.banners.ui.list;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.banners.ui.IBannersWidget;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.banners.BannerListState;
import com.inappstory.sdk.core.banners.BannersWidgetLoadStates;
import com.inappstory.sdk.core.banners.IBannersWidgetViewModel;
import com.inappstory.sdk.stories.utils.Observer;

import java.util.Objects;
import java.util.UUID;

public class BannerList extends RecyclerView implements Observer<BannerListState>, IBannersWidget {

    private String placeId;
    private IASCore core;
    private final String defaultUniqueId = UUID.randomUUID().toString();
    private String customUniqueId = null;
    private boolean initialized = false;
    private IBannersWidgetViewModel<BannerListState> bannerListViewModel;
    private BannersWidgetLoadStates currentLoadState = BannersWidgetLoadStates.EMPTY;

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
        if (customLayoutManager != null) setLayoutManager(customLayoutManager);
        super.setAdapter(adapter);
    }

    private void init() {
        defaultLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        customLayoutManager = defaultLayoutManager;
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

    @Override
    public void onUpdate(BannerListState newValue) {

    }
}
