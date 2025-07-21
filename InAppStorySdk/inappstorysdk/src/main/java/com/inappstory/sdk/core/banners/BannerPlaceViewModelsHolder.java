package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.IASCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerPlaceViewModelsHolder {
    private Map<String, IBannerPlaceViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();
    private final IASCore core;

    public BannerPlaceViewModelsHolder(IASCore core) {
        this.core = core;
    }

    public IBannerPlaceViewModel get(String bannerPlace) {
        synchronized (lock) {
            if (!viewModels.containsKey(bannerPlace)) {
                viewModels.put(bannerPlace, new BannerPlaceViewModel(core, bannerPlace));
            }
            return viewModels.get(bannerPlace);
        }
    }

    public void clear() {
        List<IBannerPlaceViewModel> placeViewModels;
        synchronized (lock) {
            placeViewModels = new ArrayList<>(viewModels.values());
        }
        for (IBannerPlaceViewModel placeViewModel : placeViewModels) {
            placeViewModel.clear();
        }
    }
}
