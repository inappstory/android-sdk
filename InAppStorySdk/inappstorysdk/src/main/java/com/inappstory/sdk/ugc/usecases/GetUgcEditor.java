package com.inappstory.sdk.ugc.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.network.content.models.SessionEditor;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.callbacks.GetSessionCallback;
import com.inappstory.sdk.ugc.dto.SessionEditorDTO;
import com.inappstory.sdk.ugc.extinterfaces.IGetUgcEditorCallback;
import com.inappstory.sdk.ugc.extinterfaces.IGetUgcEditor;

import java.lang.reflect.Type;

public class GetUgcEditor implements IGetUgcEditor {

    @Override
    public void get(final IGetUgcEditorCallback callback) {

        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                core.sessionManager().useOrOpenSession(new GetSessionCallback() {
                    @Override
                    public void onSuccess(final RequestLocalParameters sessionParameters) {
                        core.network().enqueue(
                                core.network().getApi().getUgcEditor(),
                                new NetworkCallback<SessionEditor>() {
                                    @Override
                                    public void onSuccess(SessionEditor response) {
                                        callback.get(
                                                new SessionEditorDTO(
                                                        response,
                                                        sessionParameters.sessionId
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
