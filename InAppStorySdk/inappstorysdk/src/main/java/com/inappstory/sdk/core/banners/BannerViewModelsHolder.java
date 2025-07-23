package com.inappstory.sdk.core.banners;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerViewModelsHolder {
    private Map<BannerViewModelKey, IBannerViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();
    private final IASCore core;
    IBannerPlaceViewModel bannerPlaceViewModel;

    public BannerViewModelsHolder(IASCore core, IBannerPlaceViewModel bannerPlaceViewModel) {
        this.core = core;
        this.bannerPlaceViewModel = bannerPlaceViewModel;
    }

    public void clearViewModels() {
        List<IBannerViewModel> bannerViewModels;
        synchronized (lock) {
            bannerViewModels = new ArrayList<>(viewModels.values());
        }
        for (IBannerViewModel bannerViewModel : bannerViewModels) {
            if (bannerViewModel != null) bannerViewModel.clear();
        }
    }

    public void removeViewModels() {
        synchronized (lock) {
            viewModels.clear();
        }
    }

    public IBannerViewModel get(int bannerId, String bannerPlace) {
        BannerViewModelKey key = new BannerViewModelKey(bannerId, bannerPlace);
        synchronized (lock) {
            if (!viewModels.containsKey(key)) {
                viewModels.put(key, new BannerViewModel(bannerId, bannerPlace, core, bannerPlaceViewModel));
            }
            return viewModels.get(key);
        }
    }
}
