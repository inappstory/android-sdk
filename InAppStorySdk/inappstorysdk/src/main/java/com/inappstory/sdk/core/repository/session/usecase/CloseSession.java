package com.inappstory.sdk.core.repository.session.usecase;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.ICloseSession;
import com.inappstory.sdk.stories.api.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.lang.reflect.Type;
import java.util.List;

public class CloseSession implements ICloseSession {
    private void clearCaches() {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.cachedListStories.clear();
            inAppStoryService.clearGames();
        }
    }

    @Override
    public void close(
            SessionDTO sessionDTO,
            List<List<Object>> stat
    ) {
        clearCaches();


        final String sessionCloseUID =
                ProfilingManager.getInstance().addTask("api_session_close");

        NetworkClient networkClient = InAppStoryManager.getNetworkClient();
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
