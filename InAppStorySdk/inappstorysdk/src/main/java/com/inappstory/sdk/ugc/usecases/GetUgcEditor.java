package com.inappstory.sdk.ugc.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.SessionEditor;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.ugc.dto.SessionEditorDTO;
import com.inappstory.sdk.ugc.extinterfaces.IGetUgcEditorCallback;
import com.inappstory.sdk.ugc.extinterfaces.IGetUgcEditor;

import java.lang.reflect.Type;

public class GetUgcEditor implements IGetUgcEditor {

    @Override
    public void get(final IGetUgcEditorCallback callback) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            callback.onError();
            return;
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
                    @Override
                    public void onSuccess(final String sessionId) {
                        networkClient.enqueue(
                                networkClient.getApi().getUgcEditor(),
                                new NetworkCallback<SessionEditor>() {
                                    @Override
                                    public void onSuccess(SessionEditor response) {
                                        callback.get(
                                                new SessionEditorDTO(
                                                        response,
                                                        sessionId
                                                )
                                        );
                                    }

                                    @Override
                                    public Type getType() {
                                        return SessionEditor.class;
                                    }

                                    @Override
                                    public void onError(int code, String message) {
                                        super.onError(code, message);
                                        callback.onError();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError() {
                        callback.onError();
                    }
                });
            }

            @Override
            public void error() {
                callback.onError();
            }
        });

    }
}
