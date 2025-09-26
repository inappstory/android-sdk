package com.inappstory.sdk.core.network.content.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.banners.BannerShownTime;
import com.inappstory.sdk.core.banners.BannerPlaceUseCaseCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.data.IShownTime;
import com.inappstory.sdk.core.network.content.models.BannerPlaceModel;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.TargetingBodyObject;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BannerPlaceUseCase {
    private final IASCore core;
    private final String placeId;
    private final List<String> tags;

    public BannerPlaceUseCase(IASCore core, String placeId, List<String> tags) {
        this.core = core;
        this.placeId = placeId;
        this.tags = tags;
    }

    public void get(BannerPlaceUseCaseCallback callback) {
        loadWithRetry(callback, true);
    }

    private void loadWithRetry(
            final BannerPlaceUseCaseCallback loadCallback,
            final boolean retry
    ) {
        core.statistic().profiling().addTask("banner_place");
        final IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        final List<String> localTags = new ArrayList<>();
        if (this.tags != null) {
            localTags.addAll(this.tags);
        } else {
            localTags.addAll(settingsHolder.tags());
        }


        new ConnectionCheck().check(
                core.appContext(),
                new ConnectionCheckCallback(core) {
                    @Override
                    public void success() {
                        OpenSessionCallback openSessionCallback = new OpenSessionCallback() {
                            @Override
                            public void onSuccess(final RequestLocalParameters sessionParameters) {
                                NetworkCallback<BannerPlaceModel> networkCallback = new NetworkCallback<BannerPlaceModel>() {
                                    @Override
                                    public void onSuccess(
                                            BannerPlaceModel bannerPlaceResponse
                                    ) {
                                        if (bannerPlaceResponse == null) {
                                            loadError(loadCallback);
                                            return;
                                        }
                                        boolean hasDeviceSupportedMessage = false;
                                        for (IBanner banner : bannerPlaceResponse.banners()) {
                                            hasDeviceSupportedMessage = true;
                                            core.contentHolder().readerContent().setByIdAndType(
                                                    banner, banner.id(), ContentType.BANNER
                                            );
                                        }
                                        if (!hasDeviceSupportedMessage) {
                                            loadCallback.isEmpty();
                                            return;
                                        }
                                        List<IBanner> banners = new ArrayList<>();
                                        for (IBanner banner : bannerPlaceResponse.banners()) {
                                            if (checkContentForShownFrequency(banner))
                                                banners.add(banner);
                                        }
                                        loadCallback.success(banners);
                                    }

                                    @Override
                                    public Type getType() {
                                        return BannerPlaceModel.class;
                                    }

                                    @Override
                                    public void error424(String message) {
                                        core.statistic().profiling().setReady("banner_place");
                                        core.sessionManager().closeSession(
                                                sessionParameters.anonymous(),
                                                ((IASDataSettingsHolder) core.settingsAPI()).sendStatistic(),
                                                false,
                                                sessionParameters.locale(),
                                                sessionParameters.userId(),
                                                sessionParameters.sessionId()
                                        );
                                        if (retry)
                                            loadWithRetry(loadCallback, false);
                                        else
                                            loadError(loadCallback);
                                    }
                                };
                                core.network().enqueue(
                                        core.network().getApi().getBannerPlace(
                                                placeId,
                                                1,
                                                null,
                                                "banners.slides,banners.layout",
                                                new TargetingBodyObject(
                                                        !localTags.isEmpty() ? localTags : null,
                                                        settingsHolder.options()
                                                ),
                                                sessionParameters.userId(),
                                                sessionParameters.sessionId(),
                                                sessionParameters.locale()
                                        ),
                                        networkCallback,
                                        sessionParameters
                                );
                            }

                            @Override
                            public void onError() {
                                loadError(loadCallback);
                            }
                        };
                        core.sessionManager().useOrOpenSession(
                                openSessionCallback
                        );
                    }
                }
        );
    }

    private boolean checkContentForShownFrequency(IBanner banner) {
        if (banner.displayFrom() > 0 && System.currentTimeMillis() < banner.displayFrom())
            return false;
        if (banner.displayTo() > 0 && System.currentTimeMillis() > banner.displayTo())
            return false;
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        String localOpensKey = "banner_opened";
        if (settingsHolder.userId() != null) {
            localOpensKey += settingsHolder.userId();
        }
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(localOpensKey);
        Integer openedId = null;
        Long lastTime = null;
        if (opens != null) {
            for (String open : opens) {
                IShownTime shownTime = new BannerShownTime(open);
                if (shownTime.id() == banner.id()) {
                    openedId = shownTime.id();
                    lastTime = shownTime.latestShownTime();
                }
            }
        }
        if (openedId == null) return true;
        long frequencyLimit = banner.frequencyLimit();
        if (frequencyLimit == -1) return false;
        if (frequencyLimit > 0)
            return (System.currentTimeMillis() - lastTime) >= frequencyLimit;
        return true;
    }

    private void loadError(BannerPlaceUseCaseCallback loadCallback) {
        if (loadCallback != null) {
            loadCallback.error();
        }
    }
}
