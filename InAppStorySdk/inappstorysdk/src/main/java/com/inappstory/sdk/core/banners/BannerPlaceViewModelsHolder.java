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
    private Map<String, IBannersWidgetViewModel> viewModels = new HashMap<>();
    private final Object lock = new Object();
    private final IASCore core;


    public BannerPlaceViewModelsHolder(IASCore core) {
        this.core = core;
    }


    public IBannersWidgetViewModel getContentPlaceViewModel(String bannerPlace) {
        return get("ias_banner_place_" + bannerPlace);
    }

    public IBannersWidgetViewModel getOrCreateContentPlaceViewModel(String bannerPlace) {
        return getOrCreate("ias_banner_place_" + bannerPlace, BannerWidgetViewModelType.DATA);
    }

    public void changeKey(String oldKey, String newKey) {
        synchronized (lock) {
            if (viewModels.containsKey(oldKey)) {
                viewModels.put(newKey, viewModels.remove(oldKey));
            }
        }
    }

    public Set<IBannersWidgetViewModel> getNonEmptyByPlaceId(String placeId) {
        Set<IBannersWidgetViewModel> bannerPlaceViewModels = new HashSet<>();
        synchronized (lock) {
            for (IBannersWidgetViewModel viewModel : viewModels.values()) {
                if (Objects.equals(viewModel.placeId(), placeId)) {
                    bannerPlaceViewModels.add(viewModel);
                }
            }
        }
        return bannerPlaceViewModels;
    }

    public IBannersWidgetViewModel get(String uniqueId) {
        synchronized (lock) {
            return viewModels.get(uniqueId);
        }
    }

    public IBannersWidgetViewModel getOrCreate(String uniqueId, BannerWidgetViewModelType type) {
        synchronized (lock) {
            if (!viewModels.containsKey(uniqueId)) {
                switch (type) {
                    case LAZY_LIST:
                        viewModels.put(uniqueId, new BannerListViewModel(core, uniqueId));
                        break;
                    case PAGER:
                    case DATA:
                        viewModels.put(uniqueId, new BannerCarouselViewModel(core, uniqueId));
                        break;
                }
            }
            return viewModels.get(uniqueId);
        }
    }

    public boolean copyFromCache(String uniqueId, String bannerPlace) {
        IBannerWidgetState placeState = null;
        IBannersWidgetViewModel uniqueVM = get(uniqueId);
        if (uniqueVM == null) return false;
        IBannersWidgetViewModel placeVM = getContentPlaceViewModel(bannerPlace);
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
        List<IBannersWidgetViewModel> placeViewModels;
        synchronized (lock) {
            placeViewModels = new ArrayList<>(viewModels.values());
            // viewModels.clear();
        }
        for (IBannersWidgetViewModel placeViewModel : placeViewModels) {
            placeViewModel.reloadSubscriber();
        }
    }

    public void clear() {
        List<IBannersWidgetViewModel> placeViewModels;
        synchronized (lock) {
            placeViewModels = new ArrayList<>(viewModels.values());
            viewModels.clear();
        }
        for (IBannersWidgetViewModel placeViewModel : placeViewModels) {
            placeViewModel.clear();
            placeViewModel.dataIsCleared();
        }
    }
}
