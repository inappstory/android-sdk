package com.inappstory.sdk.ugc.usecases;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.SessionEditor;
import com.inappstory.sdk.ugc.dto.SessionEditorDTO;
import com.inappstory.sdk.ugc.extinterfaces.IGetUgcEditorCallback;
import com.inappstory.sdk.ugc.extinterfaces.IGetUgcEditorUseCase;

import java.lang.reflect.Type;

public class GetUgcEditorUseCase implements IGetUgcEditorUseCase {
    @Override
    public void get(final IGetUgcEditorCallback callback) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            callback.onError();
            return;
        }
        networkClient.enqueue(
                networkClient.getApi().getUgcEditor(),
                new NetworkCallback<SessionEditor>() {
                    @Override
                    public void onSuccess(SessionEditor response) {
                        callback.get(new SessionEditorDTO(response));
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
}
