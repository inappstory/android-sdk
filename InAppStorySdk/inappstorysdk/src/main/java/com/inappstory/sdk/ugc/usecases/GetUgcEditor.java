package com.inappstory.sdk.ugc.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.network.content.models.SessionEditor;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.parse.to.ToSessionEditor;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.ugc.dto.SessionEditorDTO;
import com.inappstory.sdk.ugc.extinterfaces.IGetUgcEditorCallback;
import com.inappstory.sdk.ugc.extinterfaces.IGetUgcEditor;

public class GetUgcEditor implements IGetUgcEditor {

    @Override
    public void get(final IGetUgcEditorCallback callback) {

        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
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
                                                        sessionParameters.sessionId()
                                                )
                                        );
                                    }

                                    @Override
                                    public void onError(int code, String message) {
                                        super.onError(code, message);
                                        callback.onError();
                                    }
                                },
                                ToSessionEditor.class
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
