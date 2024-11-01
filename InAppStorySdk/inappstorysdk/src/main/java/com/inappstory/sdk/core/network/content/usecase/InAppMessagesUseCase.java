package com.inappstory.sdk.core.network.content.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InAppMessagesUseCase {
    private final IASCore core;

    public InAppMessagesUseCase(IASCore core) {
        this.core = core;
    }

    public void get(InAppMessageFeedCallback callback) {
        loadWithRetry(callback, true);
    }

    private void loadWithRetry(
            final InAppMessageFeedCallback loadCallback,
            final boolean retry
    ) {
        core.statistic().profiling().addTask("inAppMessages");
        new ConnectionCheck().check(core.appContext(), new ConnectionCheckCallback(core) {
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
                                    loadError(loadCallback);
                                    return;
                                }
                                List<IInAppMessage> messages = new ArrayList<>();
                                messages.addAll(inAppMessageFeed.messages());
                                if (messages.isEmpty()) {
                                    loadCallback.isEmpty();
                                    return;
                                }
                                for (IInAppMessage message: messages) {
                                    core.contentHolder().readerContent().setByIdAndType(
                                            message, message.id(), ContentType.IN_APP_MESSAGE
                                    );
                                }

                            }

                            @Override
                            public Type getType() {
                                return InAppMessageFeed.class;
                            }

                            @Override
                            public void error424(String message) {
                                core.statistic().profiling().setReady("inAppMessages");
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
                                    loadWithRetry(loadCallback, false);
                                else
                                    loadError(loadCallback);
                            }
                        };
                        core.network().enqueue(
                                core.network().getApi().getInAppMessages(1,
                                        null,
                                        null
                                ),
                                networkCallback
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
        });
    }

    private void loadError(InAppMessageFeedCallback loadCallback) {
        if (loadCallback != null) {
            loadCallback.error();
        }
    }
}
