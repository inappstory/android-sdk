package com.inappstory.sdk.core.api.impl;

import androidx.annotation.NonNull;

import com.inappstory.sdk.banners.BannersLoadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASBanners;
import com.inappstory.sdk.core.banners.BannerPlaceLoadStates;
import com.inappstory.sdk.core.banners.BannerPlaceState;
import com.inappstory.sdk.core.banners.IBannerPlaceViewModel;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.banners.LoadBannerPlaceCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.network.content.usecase.BannerPlaceUseCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class IASBannersImpl implements IASBanners {
    private final IASCore core;
    private Map<String, Set<BannersLoadCallback>> bannerPlacePreloadCallbacks = new HashMap<>();
    private final Object callbacksLock = new Object();
    private Map<String, BannersLoadCallback> commonCallbacks = new HashMap<>();

    public IASBannersImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void loadBannerPlace(final String bannerPlace) {
        BannerPlaceUseCase bannerPlaceUseCase = new BannerPlaceUseCase(core, bannerPlace, null);

        final IBannerPlaceViewModel bannerPagerViewModel =
                core.widgetViewModels().bannerPlaceViewModels().get(bannerPlace);
        bannerPagerViewModel.updateState(
                bannerPagerViewModel
                        .getCurrentBannerPagerState()
                        .loadState(
                                BannerPlaceLoadStates.LOADING
                        )
        );
        bannerPlaceUseCase.get(new LoadBannerPlaceCallback() {
            @Override
            public void success(List<IBanner> content) {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .iterationId(UUID.randomUUID().toString())
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannerPlaceLoadStates.EMPTY : BannerPlaceLoadStates.LOADED);
                bannerPagerViewModel.updateState(state);
            }

            @Override
            public void isEmpty() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.EMPTY);
                bannerPagerViewModel.updateState(state);
            }

            @Override
            public void error() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.FAILED);
                bannerPagerViewModel.updateState(state);
            }
        });
    }

    @Override
    public void addPreloadCallback(@NonNull BannersLoadCallback callback) {
        String bannerPlace = callback.bannerPlace();
        if (bannerPlace == null) return;
        synchronized (callbacksLock) {
            if (bannerPlacePreloadCallbacks.get(bannerPlace) == null)
                bannerPlacePreloadCallbacks.put(bannerPlace, new HashSet<BannersLoadCallback>());
            bannerPlacePreloadCallbacks.get(bannerPlace).add(callback);
            if (commonCallbacks.get(bannerPlace) == null) {
                commonCallbacks.put(
                        bannerPlace,
                        createCommonCallback(bannerPlace)
                );
            }
        }
    }

    @Override
    public void removePreloadCallback(@NonNull BannersLoadCallback callback) {
        String bannerPlace = callback.bannerPlace();
        if (bannerPlace == null) return;
        synchronized (callbacksLock) {
            if (bannerPlacePreloadCallbacks.get(bannerPlace) == null) {
                return;
            }
            bannerPlacePreloadCallbacks.get(bannerPlace).remove(callback);
        }
    }

    @Override
    public BannersLoadCallback getPreloadCallback(String bannerPlace) {
        synchronized (callbacksLock) {
            return commonCallbacks.get(bannerPlace);
        }
    }


    private BannersLoadCallback createCommonCallback(String bannerPlace) {
        return new BannersLoadCallback(bannerPlace) {
            @Override
            public void allLoaded() {
                Set<BannersLoadCallback> localCallbacks;
                synchronized (callbacksLock) {
                    localCallbacks = bannerPlacePreloadCallbacks.get(bannerPlace());
                }
                if (localCallbacks != null)
                    for (BannersLoadCallback preloadCallback: localCallbacks) {
                        preloadCallback.allLoaded();
                    }
            }

            @Override
            public void loadError() {
                Set<BannersLoadCallback> localCallbacks;
                synchronized (callbacksLock) {
                    localCallbacks = bannerPlacePreloadCallbacks.get(bannerPlace());
                }
                if (localCallbacks != null)
                    for (BannersLoadCallback preloadCallback: localCallbacks) {
                        preloadCallback.loadError();
                    }
            }

            @Override
            public void isEmpty() {
                Set<BannersLoadCallback> localCallbacks;
                synchronized (callbacksLock) {
                    localCallbacks = bannerPlacePreloadCallbacks.get(bannerPlace());
                }
                if (localCallbacks != null)
                    for (BannersLoadCallback preloadCallback: localCallbacks) {
                        preloadCallback.isEmpty();
                    }
            }
        };
    }


    @Override
    public void preload(final String bannerPlace, BannersLoadCallback callback) {
        if (bannerPlace != null) {
            if (callback != null) {
                if (!Objects.equals(bannerPlace, callback.bannerPlace())) {
                    //TODO log error
                    return;
                }
                synchronized (callbacksLock) {
                    if (bannerPlacePreloadCallbacks.get(bannerPlace) == null)
                        bannerPlacePreloadCallbacks.put(bannerPlace, new HashSet<BannersLoadCallback>());
                    bannerPlacePreloadCallbacks.get(bannerPlace).add(callback);
                    if (commonCallbacks.get(bannerPlace) == null) {
                        commonCallbacks.put(
                                bannerPlace,
                                createCommonCallback(bannerPlace)
                        );
                    }
                }
            }
        }


        BannerPlaceUseCase bannerPlaceUseCase = new BannerPlaceUseCase(core, bannerPlace, null);

        final IBannerPlaceViewModel bannerPagerViewModel =
                core.widgetViewModels().bannerPlaceViewModels().get(bannerPlace);
        bannerPagerViewModel.updateState(
                bannerPagerViewModel
                        .getCurrentBannerPagerState()
                        .loadState(
                                BannerPlaceLoadStates.LOADING
                        )
        );
        bannerPlaceUseCase.get(new LoadBannerPlaceCallback() {
            @Override
            public void success(List<IBanner> content) {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .iterationId(UUID.randomUUID().toString())
                        .items(content)
                        .loadState(
                                content.isEmpty() ? BannerPlaceLoadStates.EMPTY : BannerPlaceLoadStates.LOADED);
                bannerPagerViewModel.updateState(state);

                List<IBannerViewModel> bannerViewModels = bannerPagerViewModel.getBannerViewModels();
                for (IBannerViewModel bannerViewModel : bannerViewModels) {
                    bannerViewModel.loadContent();
                }
            }

            @Override
            public void isEmpty() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.EMPTY);
                bannerPagerViewModel.updateState(state);
            }

            @Override
            public void error() {
                BannerPlaceState state = bannerPagerViewModel.getCurrentBannerPagerState()
                        .copy()
                        .items(new ArrayList<IBanner>())
                        .loadState(
                                BannerPlaceLoadStates.FAILED);
                bannerPagerViewModel.updateState(state);
            }
        });
    }
}
