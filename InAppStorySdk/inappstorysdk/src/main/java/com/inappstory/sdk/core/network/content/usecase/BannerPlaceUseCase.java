package com.inappstory.sdk.core.network.content.usecase;

import android.text.TextUtils;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.banners.LoadBannerPlaceCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.network.content.mock.MockBanners;
import com.inappstory.sdk.core.network.content.models.BannerPlace;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.BannerPlaceFilterObject;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.utils.TagsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BannerPlaceUseCase {
    private final IASCore core;
    private final String placeId;
    private final List<String> tags;

    public BannerPlaceUseCase(IASCore core, String placeId, List<String> tags) {
        this.core = core;
        this.placeId = placeId;
        this.tags = tags;
    }

    public void get(LoadBannerPlaceCallback callback) {
        loadWithRetry(callback, true);
    }

    private void loadWithRetry(
            final LoadBannerPlaceCallback loadCallback,
            final boolean retry
    ) {
        core.statistic().profiling().addTask("banner_place");
        final List<String> localTags = new ArrayList<>();
        if (this.tags != null) {
            localTags.addAll(this.tags);
        } else {
            localTags.addAll(((IASDataSettingsHolder) core.settingsAPI()).tags());
        }

        new ConnectionCheck().check(
                core.appContext(),
                new ConnectionCheckCallback(core) {
                    @Override
                    public void success() {
                        OpenSessionCallback openSessionCallback = new OpenSessionCallback() {
                            @Override
                            public void onSuccess(final RequestLocalParameters sessionParameters) {
                                NetworkCallback<BannerPlace> networkCallback = new NetworkCallback<BannerPlace>() {
                                    @Override
                                    public void onSuccess(
                                            BannerPlace bannerPlaceResponse
                                    ) {
                                        if (bannerPlaceResponse == null) {
                                            loadError(loadCallback);
                                            return;
                                        }
                                        /*BannerPlace bannerPlaceResponse =
                                                new MockBanners().getMockBannerPlace(place.banners(), placeId);*/
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
                                        List<IReaderContent> nonCasted = core.contentHolder()
                                                .readerContent()
                                                .getByType(ContentType.BANNER);
                                        if (nonCasted != null) {
                                            for (IReaderContent readerContent : nonCasted) {
                                                if (readerContent instanceof IBanner)
                                                    banners.add((IBanner) readerContent);
                                            }
                                        }
                                        loadCallback.success(banners);
                                    }

                                    @Override
                                    public Type getType() {
                                        return BannerPlace.class;
                                    }

                                    @Override
                                    public void error424(String message) {
                                        core.statistic().profiling().setReady("banner_place");
                                        core.sessionManager().closeSession(
                                                true,
                                                false,
                                                sessionParameters.locale,
                                                sessionParameters.userId,
                                                sessionParameters.sessionId
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
                                                "banners.slides",
                                                new BannerPlaceFilterObject(localTags),
                                                sessionParameters.userId,
                                                sessionParameters.sessionId,
                                                sessionParameters.locale
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

    private void loadError(LoadBannerPlaceCallback loadCallback) {
        if (loadCallback != null) {
            loadCallback.error();
        }
    }
}
