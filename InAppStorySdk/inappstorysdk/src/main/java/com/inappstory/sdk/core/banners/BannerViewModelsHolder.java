package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.IASCore;

import java.util.HashMap;
import java.util.Map;

public class BannerViewModelsHolder {
    private Map<Integer, IBannerViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();
    private final IASCore core;

    public BannerViewModelsHolder(IASCore core) {
        this.core = core;
    }

    public IBannerViewModel get(int bannerId) {
        synchronized (lock) {
            if (!viewModels.containsKey(bannerId)) {
                viewModels.put(bannerId, new BannerViewModel(bannerId, core));
            }
            return viewModels.get(bannerId);
        }
    }
}
