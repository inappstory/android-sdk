package com.inappstory.sdk.core.network.content.usecase;

import android.text.TextUtils;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.TargetingBodyObject;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.utils.TagsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InAppMessagesUseCase {
    private final IASCore core;
    private final String ids;
    private final List<String> tags;

    public InAppMessagesUseCase(IASCore core, String ids, List<String> tags) {
        this.core = core;
        this.ids = ids;
        this.tags = tags;
    }

    public void get(InAppMessageFeedCallback callback) {
        loadWithRetry(callback, true);
    }

    private void loadWithRetry(
            final InAppMessageFeedCallback loadCallback,
            final boolean retry
    ) {
        core.statistic().profiling().addTask("inAppMessages");
        final IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
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
                                NetworkCallback<InAppMessageFeed> networkCallback = new NetworkCallback<InAppMessageFeed>() {
                                    @Override
                                    public void onSuccess(
                                            InAppMessageFeed inAppMessageFeed
                                    ) {
                                        if (inAppMessageFeed == null) {
                                            loadError(loadCallback);
                                            return;
                                        }
                                        core.contentLoader().changeIamWereLoadedStatus(TagsUtils.tagsHash(localTags));
                                        boolean hasDeviceSupportedMessage = false;
                                        for (IInAppMessage message : inAppMessageFeed.messages()) {
                                            hasDeviceSupportedMessage = true;
                                            core.contentHolder().readerContent().setByIdAndType(
                                                    message, message.id(), ContentType.IN_APP_MESSAGE
                                            );
                                        }
                                        if (!hasDeviceSupportedMessage) {
                                            loadCallback.isEmpty();
                                            return;
                                        }
                                        loadCallback.success(
                                                core.contentHolder()
                                                        .readerContent()
                                                        .getByType(ContentType.IN_APP_MESSAGE)
                                        );
                                    }

                                    @Override
                                    public Type getType() {
                                        return InAppMessageFeed.class;
                                    }

                                    @Override
                                    public void error424(String message) {
                                        core.statistic().profiling().setReady("inAppMessages");
                                        core.sessionManager().closeSession(
                                                sessionParameters.anonymous(),
                                                sessionParameters.sendStatistic(),
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
                                        core.network().getApi().getInAppMessages(
                                                1,
                                                ids,
                                                new TargetingBodyObject(
                                                        !localTags.isEmpty() ? localTags : null,
                                                        settingsHolder.options()
                                                ),
                                                null,
                                                "messages.slides",
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

    private void loadError(InAppMessageFeedCallback loadCallback) {
        if (loadCallback != null) {
            loadCallback.error();
        }
    }
}
