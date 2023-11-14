package com.inappstory.sdk.core.repository.session.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.utils.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.ICloseSession;
import com.inappstory.sdk.core.models.api.SessionResponse;
import com.inappstory.sdk.core.models.StatisticSendObject;
import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.core.repository.statistic.ProfilingManager;

import java.lang.reflect.Type;
import java.util.List;

public class CloseSession implements ICloseSession {
    private void clearCaches() {
        IASCore.getInstance().getStoriesRepository(Story.StoryType.COMMON).clearCachedLists();
        IASCore.getInstance().gameRepository.clearGames();
    }

    @Override
    public void close(
            SessionDTO sessionDTO,
            List<List<Object>> stat
    ) {
        clearCaches();


        final String sessionCloseUID =
                ProfilingManager.getInstance().addTask("api_session_close");

        NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null) {
            return;
        }
        networkClient.enqueue(
                networkClient.getApi().sessionClose(
                        new StatisticSendObject(
                                sessionDTO.getId(),
                                stat
                        )
                ),
                new NetworkCallback<SessionResponse>() {
                    @Override
                    public void onSuccess(SessionResponse response) {
                        ProfilingManager.getInstance().setReady(sessionCloseUID, true);
                    }

                    @Override
                    public Type getType() {
                        return SessionResponse.class;
                    }

                    @Override
                    public void errorDefault(String message) {
                        ProfilingManager.getInstance().setReady(sessionCloseUID);
                    }
                }
        );
    }
}
