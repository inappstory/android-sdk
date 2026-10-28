package com.inappstory.sdk.core.banners;



import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.banners.BannerPlaceLoadSettings;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.stories.utils.Observer;

import java.util.List;

public class BannerPreviewViewModel extends BannerCarouselViewModel {
    public BannerPreviewViewModel(IASCore core, String uniqueId) {
        super(core, uniqueId);
    }

    @Override
    public void loadBanners(boolean skipCache) {
        if (!skipCache) {
            if (core.widgetViewModels().bannerPlaceViewModels().copyFromCache(uniqueId, placeId))
                return;
        }
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.bannersAPI().loadPreviewBannerPlace(
                        new BannerPlaceLoadSettings()
                                .widgetUID(widgetUID)
                                .placeId(placeId)
                                .uniqueId(uniqueId)
                );
            }
        });
    }


    @Override
    public void reloadSubscriber() {
        List<Observer<BannerCarouselState>> subscribers = bannerPlaceStateObservable.getSubscribers();
        boolean hasUISubs = false;
        for (Observer<BannerCarouselState> subscriber : subscribers) {
            if (subscriber == localObserver) continue;
            hasUISubs = true;
        }
        clear();
        if (hasUISubs) {
            InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.bannersAPI().loadPreviewBannerPlace(
                            new BannerPlaceLoadSettings()
                                    .placeId(placeId)
                                    .uniqueId(uniqueId)
                    );
                }
            });
        }
    }
}
