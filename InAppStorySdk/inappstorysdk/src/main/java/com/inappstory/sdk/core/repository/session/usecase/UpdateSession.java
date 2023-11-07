package com.inappstory.sdk.core.repository.session.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IUpdateSession;
import com.inappstory.sdk.core.repository.session.interfaces.IUpdateSessionCallback;
import com.inappstory.sdk.stories.api.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.lang.reflect.Type;
import java.util.List;

public class UpdateSession implements IUpdateSession {
    @Override
    public void update(SessionDTO session, final List<List<Object>> stat, final IUpdateSessionCallback callback) {
        final NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null || session == null) {
            callback.onError();
            return;
        }
        final String updateUUID = ProfilingManager.getInstance().addTask(
                "api_session_update"
        );
        networkClient.enqueue(
                networkClient.getApi().sessionUpdate(
                        new StatisticSendObject(
                                session.getId(),
                                stat
                        )
                ),
                new NetworkCallback<SessionResponse>() {
                    @Override
                    public void onSuccess(SessionResponse response) {
                        ProfilingManager.getInstance().setReady(updateUUID);
                        callback.onSuccess(null);
                    }

                    @Override
                    public void errorDefault(String message) {
                        ProfilingManager.getInstance().setReady(updateUUID);
                        callback.onError();
                    }

                    @Override
                    public Type getType() {
                        return SessionResponse.class;
                    }
                }
        );
    }
}
