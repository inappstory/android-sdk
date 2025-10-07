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
    private Map<BannerPlaceViewModelKey, IBannerPlaceViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();
    private final IASCore core;


    public BannerPlaceViewModelsHolder(IASCore core) {
        this.core = core;
    }

    public IBannerPlaceViewModel getById(String uniqueId) {
        synchronized (lock) {
            Set<BannerPlaceViewModelKey> keys = viewModels.keySet();
            BannerPlaceViewModelKey searchKey = null;
            for (BannerPlaceViewModelKey key : keys) {
                if (Objects.equals(key.uniqueId, uniqueId)) {
                    searchKey = key;
                    break;
                }
            }
            if (searchKey == null) return null;
            return viewModels.get(searchKey);
        }
    }

    public IBannerPlaceViewModel getByBannerPlace(String bannerPlace) {
        return getOrCreateWithCopy("", bannerPlace);
    }


    public Set<IBannerPlaceViewModel> getNonEmptyByBannerPlace(String bannerPlace) {
        Set<BannerPlaceViewModelKey> keys = viewModels.keySet();
        Set<IBannerPlaceViewModel> bannerPlaceViewModels = new HashSet<>();
        for (BannerPlaceViewModelKey key : keys) {
            if (Objects.equals(key.bannerPlace, bannerPlace)) {
                bannerPlaceViewModels.add(viewModels.get(key));
            }
        }
        return bannerPlaceViewModels;
    }

    public IBannerPlaceViewModel getOrCreateWithoutCopy(String uniqueId, String bannerPlace) {
        IBannerPlaceViewModel newVM;
        synchronized (lock) {
            BannerPlaceViewModelKey key = new BannerPlaceViewModelKey(uniqueId, bannerPlace);
            if (!viewModels.containsKey(key)) {
                newVM = new BannerPlaceViewModel(core, bannerPlace, uniqueId);
                viewModels.put(key, newVM);
            } else {
                newVM = viewModels.get(key);
            }
        }
        return newVM;
    }

    public IBannerPlaceViewModel get(String uniqueId, String bannerPlace) {
        synchronized (lock) {
            BannerPlaceViewModelKey key = new BannerPlaceViewModelKey(uniqueId, bannerPlace);
            return viewModels.get(key);
        }
    }

    public IBannerPlaceViewModel getOrCreateWithCopy(String uniqueId, String bannerPlace) {
        BannerPlaceViewModelKey emptyKey;
        BannerPlaceState placeState = null;
        IBannerPlaceViewModel newVM;
        synchronized (lock) {
            BannerPlaceViewModelKey key = new BannerPlaceViewModelKey(uniqueId, bannerPlace);
            emptyKey = new BannerPlaceViewModelKey("", bannerPlace);
            if (!viewModels.containsKey(key)) {
                newVM = new BannerPlaceViewModel(core, bannerPlace, uniqueId);
                viewModels.put(key, newVM);
                if (!uniqueId.isEmpty()) {
                    IBannerPlaceViewModel placeVM = viewModels.get(emptyKey);
                    if (placeVM != null) placeState = placeVM.getCurrentBannerPlaceState();
                }
            } else {
                newVM = viewModels.get(key);
            }
        }
        if (!uniqueId.isEmpty() && placeState != null) {
            newVM.updateState(
                    placeState
                            .copy()
                            .currentIndex(0)
                            .iterationId(
                                    UUID.randomUUID().toString()
                            )
            );
        }
        return newVM;
    }

    public void reloadSession() {
        List<IBannerPlaceViewModel> placeViewModels;
        synchronized (lock) {
            placeViewModels = new ArrayList<>(viewModels.values());
            // viewModels.clear();
        }
        for (IBannerPlaceViewModel placeViewModel : placeViewModels) {
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
