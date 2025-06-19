package com.inappstory.sdk.core.network.content.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.inappmessages.InAppMessageByIdCallback;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.inappmessage.IAMUiContainerType;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.lang.reflect.Type;

public class InAppMessageByIdUseCase {
    private final IASCore core;
    private final int id;

    public InAppMessageByIdUseCase(IASCore core, int id) {
        this.core = core;
        this.id = id;
    }

    public void get(final InAppMessageByIdCallback callback) {
        new ConnectionCheck().check(
                core.appContext(),
                new ConnectionCheckCallback(core) {
                    @Override
                    public void success() {
                        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
                            @Override
                            public void onSuccess(RequestLocalParameters sessionParameters) {
                                NetworkClient networkClient = core.network();
                                networkClient.enqueue(
                                        networkClient.getApi().getInAppMessage(
                                                Integer.toString(id),
                                                1,
                                                null,
                                                "slides,layout",
                                                sessionParameters.userId,
                                                sessionParameters.sessionId,
                                                sessionParameters.locale
                                        ),
                                        new NetworkCallback<InAppMessage>() {
                                            @Override
                                            public void onSuccess(InAppMessage response) {
                                                core.contentHolder().readerContent().setByIdAndType(
                                                        response,
                                                        response.id(),
                                                        ContentType.IN_APP_MESSAGE
                                                );
                                                callback.success(response);
                                               /* if (
                                                        core.screensManager().isPhone() ||
                                                        response.screenType().equals(IAMUiContainerType.POPUP)
                                                ) {

                                                } else {
                                                    callback.error();
                                                }*/
                                            }

                                            @Override
                                            public Type getType() {
                                                return InAppMessage.class;
                                            }
                                        },
                                        sessionParameters
                                );
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
