package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.IASCore;

import java.util.HashMap;
import java.util.Map;

public class BannerPagerViewModelsHolder {
    private Map<String, IBannerPagerViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();
    private final IASCore core;

    public BannerPagerViewModelsHolder(IASCore core) {
        this.core = core;
    }

    public IBannerPagerViewModel get(String bannerPlace) {
        synchronized (lock) {
            if (!viewModels.containsKey(bannerPlace)) {
                viewModels.put(bannerPlace, new BannerPagerViewModel(core, bannerPlace));
            }
            return viewModels.get(bannerPlace);
        }
    }
}
