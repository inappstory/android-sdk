package com.inappstory.sdk.core.banners;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public void removeViewModel(IBannerViewModel bannerViewModel) {
        synchronized (lock) {
            Iterator<Map.Entry<BannerViewModelKey, IBannerViewModel>> iterator = viewModels.entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue() == bannerViewModel) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public List<IBannerViewModel> get(int bannerId, String bannerPlace) {
        List<IBannerViewModel> result = new ArrayList<>();
        synchronized (lock) {
            Set<BannerViewModelKey> allKeys = viewModels.keySet();
            for (BannerViewModelKey key : allKeys) {
                if (key.correct(bannerId, bannerPlace)) {
                    result.add(viewModels.get(key));
                }
            }
        }
        return result;
    }

    public IBannerViewModel get(int bannerId, int bannerIndex, String bannerPlace) {
        BannerViewModelKey key = new BannerViewModelKey(bannerId, bannerIndex, bannerPlace);
        synchronized (lock) {
            if (!viewModels.containsKey(key)) {
                viewModels.put(key,
                        new BannerViewModel(
                                bannerId,
                                bannerPlace,
                                bannerIndex,
                                core,
                                bannerPlaceViewModel
                        )
                );
            }
            return viewModels.get(key);
        }
    }
}
