package com.inappstory.sdk.core.banners;

import java.util.HashMap;
import java.util.Map;

public class BannerPagerViewModelsHolder {
    private Map<Integer, IBannerViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();

    public IBannerViewModel get(int bannerId) {
        synchronized (lock) {
            if (!viewModels.containsKey(bannerId)) {
                viewModels.put(bannerId, new BannerViewModel(bannerId));
            }
            return viewModels.get(bannerId);
        }
    }
}
