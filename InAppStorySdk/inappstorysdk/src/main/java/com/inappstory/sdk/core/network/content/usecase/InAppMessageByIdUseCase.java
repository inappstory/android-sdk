package com.inappstory.sdk.core.network.content.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.inappmessages.InAppMessageByIdCallback;
import com.inappstory.sdk.core.network.content.RequestFields;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.TargetingBodyObject;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.utils.TagsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InAppMessageByIdUseCase {
    private final IASCore core;
    private final int id;
    private final List<String> tags;
    private final boolean useTargeting;

    public InAppMessageByIdUseCase(IASCore core, int id, boolean useTargeting, List<String> tags) {
        this.core = core;
        this.id = id;
        this.useTargeting = useTargeting;
        if (tags != null && !tags.isEmpty()) {
            this.tags = new ArrayList<>(tags);
        } else
            this.tags = null;
    }

    public void get(final InAppMessageByIdCallback callback) {
        final IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        new ConnectionCheck().check(
                core.appContext(),
                new ConnectionCheckCallback(core) {
                    @Override
                    public void success() {
                        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
                            @Override
                            public void onSuccess(RequestLocalParameters requestLocalParameters) {
                                NetworkClient networkClient = core.network();
                                NetworkCallback<InAppMessage> iamCallback = new NetworkCallback<InAppMessage>() {
                                    @Override
                                    public void onSuccess(InAppMessage response) {
                                        core.contentHolder().readerContent().setByIdAndType(
                                                response,
                                                response.id(),
                                                ContentType.IN_APP_MESSAGE
                                        );
                                        callback.success(response);
                                    }

                                    @Override
                                    public Type getType() {
                                        return InAppMessage.class;
                                    }

                                    @Override
                                    public void onError(int code, String message) {
                                        callback.error();
                                    }
                                };
                                if (useTargeting)
                                    networkClient.enqueue(
                                            networkClient.getApi().getInAppMessageWithTargeting(
                                                    Integer.toString(id),
                                                    1,
                                                    RequestFields.IAM_FIELDS,
                                                    RequestFields.IAM_EXPAND,
                                                    new TargetingBodyObject(
                                                            tags,
                                                            settingsHolder.options()
                                                    ),
                                                    requestLocalParameters.userId(),
                                                    requestLocalParameters.sessionId(),
                                                    requestLocalParameters.locale()
                                            ),
                                            iamCallback,
                                            requestLocalParameters
                                    );
                                else {
                                    networkClient.enqueue(
                                            networkClient.getApi().getInAppMessage(
                                                    Integer.toString(id),
                                                    1,
                                                    RequestFields.IAM_FIELDS,
                                                    RequestFields.IAM_EXPAND,
                                                    requestLocalParameters.userId(),
                                                    requestLocalParameters.sessionId(),
                                                    requestLocalParameters.locale()
                                            ),
                                            iamCallback,
                                            requestLocalParameters
                                    );
                                }
                            }

                            @Override
                            public void onError() {
                                callback.error();
                            }
                        });
                    }

                    @Override
                    protected void error() {
                        callback.error();
                    }
                }
        );
    }
}
