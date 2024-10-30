package com.inappstory.sdk.core.network.content.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.lang.reflect.Type;

public class InAppMessagesUseCase {
    private final IASCore core;

    public InAppMessagesUseCase(IASCore core) {
        this.core = core;
    }

    public void get() {
        loadWithRetry(callback, true);
    }

    private void loadWithRetry(final boolean retry) {
        new ConnectionCheck().check(core.appContext(), new ConnectionCheckCallback() {
            @Override
            public void success() {
                OpenSessionCallback openSessionCallback = new OpenSessionCallback() {
                    @Override
                    public void onSuccess(final String sessionId) {
                        NetworkCallback<InAppMessageFeed> networkCallback = new NetworkCallback<InAppMessageFeed>() {
                            @Override
                            public void onSuccess(
                                    InAppMessageFeed inAppMessageFeed
                            ) {
                                if (inAppMessageFeed == null) {
                                    loadError();
                                    return;
                                }

                            }

                            @Override
                            public Type getType() {
                                return InAppMessageFeed.class;
                            }

                            @Override
                            public void error424(String message) {
                                core.statistic().profiling().setReady(loadStoriesUID);
                                loadError();

                                IASDataSettingsHolder dataSettingsHolder =
                                        (IASDataSettingsHolder) core.settingsAPI();
                                core.sessionManager().closeSession(
                                        true,
                                        false,
                                        dataSettingsHolder.lang(),
                                        dataSettingsHolder.userId(),
                                        sessionId
                                );
                                if (retry)
                                    loadWithRetry(callback, false);
                            }
                        };
                        core.network().enqueue(
                                core.network().getApi().getInAppMessages(1, null, null),
                                networkCallback
                        );
                    }

                    @Override
                    public void onError() {
                        loadError();
                    }
                };
                core.sessionManager().useOrOpenSession(
                        openSessionCallback
                );
            }
        });
    }

    private void loadError() {
    }
}
