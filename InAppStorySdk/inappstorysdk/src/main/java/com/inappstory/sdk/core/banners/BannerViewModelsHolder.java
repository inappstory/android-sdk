package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.IASCore;

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
