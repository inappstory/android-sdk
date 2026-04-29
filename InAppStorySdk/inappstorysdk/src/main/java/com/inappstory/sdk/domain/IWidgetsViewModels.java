package com.inappstory.sdk.domain;

import com.inappstory.sdk.core.banners.BannerPlaceViewModelsHolder;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListViewModelsHolder;

public interface IWidgetsViewModels {
    BannerPlaceViewModelsHolder bannerPlaceViewModels();
    StoriesListViewModelsHolder storiesListViewModels();
}
