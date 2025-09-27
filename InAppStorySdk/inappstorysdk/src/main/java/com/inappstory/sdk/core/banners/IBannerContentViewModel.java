package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.banners.BannerPlacePreloadCallback;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.utils.Observer;

public interface IBannerContentViewModel extends IReaderSlideViewModel {
    BannerContentState getCurrentBannerContentState();
    BannerData getCurrentBannerData();

    void addSubscriber(Observer<BannerContentState> observer);
    void removeSubscriber(Observer<BannerContentState> observer);
    void updateCurrentLoadState(BannerLoadStates bannerLoadState);

    boolean loadContent(boolean isFirst, BannerPlacePreloadCallback callback);

    void clear();
}
