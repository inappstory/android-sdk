package com.inappstory.sdk.domain;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.banners.BannerPlaceViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerViewModelsHolder;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListViewModelsHolder;

public class WidgetsViewModels implements IWidgetsViewModels {

    private final BannerPlaceViewModelsHolder bannerPlaceViewModelsHolder;
    private final StoriesListViewModelsHolder storiesListViewModelsHolder;
    private final IASCore core;

    public WidgetsViewModels(IASCore core) {
        this.core = core;
        bannerPlaceViewModelsHolder = new BannerPlaceViewModelsHolder(core);
        storiesListViewModelsHolder = new StoriesListViewModelsHolder(core);
    }

    @Override
    public BannerPlaceViewModelsHolder bannerPlaceViewModels() {
        return bannerPlaceViewModelsHolder;
    }

    @Override
    public StoriesListViewModelsHolder storiesListViewModels() {
        return storiesListViewModelsHolder;
    }
}
