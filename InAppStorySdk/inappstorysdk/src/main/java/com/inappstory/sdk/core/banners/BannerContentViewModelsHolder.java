package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.IASCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerContentViewModelsHolder {
    private final Map<Integer, IBannerContentViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();
    private final IASCore core;

    public BannerContentViewModelsHolder(IASCore core) {
        this.core = core;
    }

    public void clearViewModels() {
        List<IBannerContentViewModel> bannerViewModels;
        synchronized (lock) {
            bannerViewModels = new ArrayList<>(viewModels.values());
        }
        for (IBannerContentViewModel bannerViewModel : bannerViewModels) {
            if (bannerViewModel != null) bannerViewModel.clear();
        }
    }

    public void removeViewModels() {
        synchronized (lock) {
            viewModels.clear();
        }
    }

    public IBannerContentViewModel get(
            int bannerId,
            IBannerContentViewModelCreator creator
    ) {
        synchronized (lock) {
            if (!viewModels.containsKey(bannerId) && creator != null) {
                viewModels.put(bannerId, creator.create(core));
            }
            return viewModels.get(bannerId);
        }
    }
}
