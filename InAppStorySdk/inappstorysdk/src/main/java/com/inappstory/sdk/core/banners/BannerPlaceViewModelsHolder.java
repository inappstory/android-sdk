package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.IASCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BannerPlaceViewModelsHolder {
    private Map<String, IBannerPlaceViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();
    private final IASCore core;


    public BannerPlaceViewModelsHolder(IASCore core) {
        this.core = core;
    }


    public IBannerPlaceViewModel getContentPlaceViewModel(String bannerPlace) {
        return get("ias_banner_place_" + bannerPlace);
    }

    public IBannerPlaceViewModel getOrCreateContentPlaceViewModel(String bannerPlace) {
        return getOrCreate("ias_banner_place_" + bannerPlace);
    }

    public void changeKey(String oldKey, String newKey) {
        synchronized (lock) {
            if (viewModels.containsKey(oldKey)) {
                viewModels.put(newKey, viewModels.remove(oldKey));
            }
        }
    }

    public Set<IBannerPlaceViewModel> getNonEmptyByPlaceId(String placeId) {
        Set<IBannerPlaceViewModel> bannerPlaceViewModels = new HashSet<>();
        synchronized (lock) {
            for (IBannerPlaceViewModel viewModel : viewModels.values()) {
                if (Objects.equals(viewModel.placeId(), placeId)) {
                    bannerPlaceViewModels.add(viewModel);
                }
            }
        }
        return bannerPlaceViewModels;
    }

    public IBannerPlaceViewModel get(String uniqueId) {
        synchronized (lock) {
            return viewModels.get(uniqueId);
        }
    }

    public IBannerPlaceViewModel getOrCreate(String uniqueId) {
        synchronized (lock) {
            if (!viewModels.containsKey(uniqueId)) {
                viewModels.put(uniqueId, new BannerPlaceViewModel(core, uniqueId));
            }
            return viewModels.get(uniqueId);
        }
    }

    public boolean copyFromCache(String uniqueId, String bannerPlace) {
        BannerPlaceState placeState = null;
        IBannerPlaceViewModel uniqueVM = get(uniqueId);
        if (uniqueVM == null) return false;
        IBannerPlaceViewModel placeVM = getContentPlaceViewModel(bannerPlace);
        if (placeVM != null)
            placeState = placeVM.getCurrentBannerPlaceState();
        if (placeState != null) {
            uniqueVM.updateState(
                    placeState
                            .copy()
                           // .currentIndex(0)
                            .iterationId(
                                    UUID.randomUUID().toString()
                            )
            );
            return true;
        }
        return false;
    }

    public void reloadSession() {
        List<IBannerPlaceViewModel> placeViewModels;
        synchronized (lock) {
            placeViewModels = new ArrayList<>(viewModels.values());
            // viewModels.clear();
        }
        for (IBannerPlaceViewModel placeViewModel : placeViewModels) {
            if (placeViewModel == null) continue;
            placeViewModel.reloadSubscriber();
        }
    }

    public void clear() {
        List<IBannerPlaceViewModel> placeViewModels;
        synchronized (lock) {
            placeViewModels = new ArrayList<>(viewModels.values());
            viewModels.clear();
        }
        for (IBannerPlaceViewModel placeViewModel : placeViewModels) {
            placeViewModel.clear();
            placeViewModel.dataIsCleared();
        }
    }
}
