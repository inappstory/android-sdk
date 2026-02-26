package com.inappstory.sdk.refactoring.session.repositories;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.refactoring.core.utils.models.Error;
import com.inappstory.sdk.refactoring.core.utils.models.NoSessionError;
import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;
import com.inappstory.sdk.refactoring.session.data.mappers.NSessionToSessionDTOMapper;
import com.inappstory.sdk.refactoring.session.data.network.NSession;
import com.inappstory.sdk.refactoring.session.repositories.datasources.SessionLocalDataSource;
import com.inappstory.sdk.refactoring.session.repositories.datasources.SessionAPIDataSource;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SessionRepository implements ISessionRepository {
    private final SessionAPIDataSource sessionAPIDataSource;
    private final SessionLocalDataSource sessionLocalDataSource;
    private final IASCore core;
    private final Object sessionParametersLock = new Object();
    private String latestSessionId;
    private String latestDeviceId;
    private boolean getSessionInProcess = false;
    private final Object getSessionLock = new Object();
    private final Set<ResultCallback<SessionDTO>> getSessionCallbacks = new HashSet<>();
    private UniqueSessionParameters currentParameters = null;
    private final ISessionSubscribersHolder subscribersHolder;

    public SessionRepository(
            IASCore core,
            ISessionSubscribersHolder subscribersHolder
    ) {
        this.core = core;
        this.sessionAPIDataSource = new SessionAPIDataSource(core);
        this.sessionLocalDataSource = new SessionLocalDataSource();
        this.subscribersHolder = subscribersHolder;
    }

    @Override
    public void getSession(ResultCallback<SessionDTO> getSessionCallback) {
        synchronized (getSessionLock) {
            getSessionCallbacks.add(getSessionCallback);
            if (getSessionInProcess) return;
            getSessionInProcess = true;
        }
        SessionDTO localSession = sessionLocalDataSource.getSession();
        if (localSession == null) {
            getRemoteSession();
        } else {
            getSessionSuccess(localSession);
        }
    }

    private void getRemoteSession() {
        final UniqueSessionParameters sessionParameters;
        synchronized (sessionParametersLock) {
            sessionParameters = currentParameters;
        }
        if (sessionParameters == null) {
            getSessionError(new Error<>("Wrong session parameters"));
            return;
        }
        ResultCallback<NSession> callback = new ResultCallback<NSession>() {
            @Override
            public void success(NSession result) {
                synchronized (sessionParametersLock) {
                    if (!Objects.equals(currentParameters, sessionParameters)) {
                        getRemoteSession();
                        return;
                    }
                }
                SessionDTO sessionDTO = new NSessionToSessionDTOMapper().convert(result);
                synchronized (sessionParametersLock) {
                    latestSessionId = sessionDTO.sessionId();
                    latestDeviceId = ((IASDataSettingsHolder) core.settingsAPI()).deviceId();
                }
                sessionLocalDataSource.setSession(sessionDTO);
                getSessionSuccess(sessionDTO);
            }

            @Override
            public void error(Error<NSession> result) {
                getSessionError(new NoSessionError<>());
            }
        };
        sessionAPIDataSource.getSession(sessionParameters, callback);
    }

    private void getSessionSuccess(SessionDTO sessionDTO) {
        List<ResultCallback<SessionDTO>> callbacks;
        synchronized (getSessionLock) {
            callbacks = new ArrayList<>(getSessionCallbacks);
            getSessionCallbacks.clear();
            getSessionInProcess = false;
        }
        for (ResultCallback<SessionDTO> callback : callbacks) {
            callback.success(sessionDTO);
        }
    }

    private void getSessionError(Error error) {
        List<ResultCallback<SessionDTO>> callbacks;
        synchronized (getSessionLock) {
            callbacks = new ArrayList<>(getSessionCallbacks);
            getSessionCallbacks.clear();
            getSessionInProcess = false;
        }
        for (ResultCallback<SessionDTO> callback : callbacks) {
            callback.error(error);
        }
    }

    @Override
    public void updateSession(ResultCallback<Void> updateSessionCallback) {
        UniqueSessionParameters tempSessionParameters;
        final String sessionId;
        synchronized (sessionParametersLock) {
            tempSessionParameters = currentParameters;
            sessionId = latestSessionId;
        }
        if (tempSessionParameters == null) return;
        if (tempSessionParameters.anonymous() || !tempSessionParameters.sendStatistic()) return;
        core.statistic().storiesV1(sessionId, new GetStatisticV1Callback() {
            @Override
            public void get(@NonNull IASStatisticStoriesV1 manager) {
                List<List<Object>> data = manager.extractCurrentStatistic();
                if (data.isEmpty()) return;
                StatisticSendObject statisticSendObject =
                        new StatisticSendObject(sessionId, data);
                sessionAPIDataSource.updateSession(
                        statisticSendObject,
                        tempSessionParameters,
                        updateSessionCallback
                );
            }
        });

    }

    @Override
    public void closeSession(
            final UniqueSessionParameters sessionParameters,
            ResultCallback<Void> closeSessionCallback
    ) {
        final String sessionId;
        final String deviceId;
        synchronized (sessionParametersLock) {
            sessionId = latestSessionId;
            deviceId = latestDeviceId;
        }
        if (sessionParameters == null) return;
        if (sessionParameters.anonymous() || !sessionParameters.sendStatistic()) return;
        core.statistic().storiesV1(sessionId, new GetStatisticV1Callback() {
            @Override
            public void get(@NonNull IASStatisticStoriesV1 manager) {
                List<List<Object>> data = manager.extractCurrentStatistic();
                if (data.isEmpty()) return;
                StatisticSendObject statisticSendObject =
                        new StatisticSendObject(sessionId, data);
                sessionAPIDataSource.closeSession(
                        statisticSendObject,
                        sessionId,
                        deviceId,
                        sessionParameters,
                        closeSessionCallback
                );
            }
        });
    }

    @Override
    public void newSessionParameters(
            UniqueSessionParameters sessionParameters,
            ResultCallback<Void> closeSessionCallback
    ) {
        UniqueSessionParameters oldSessionParameters;
        final String sessionId;
        synchronized (sessionParametersLock) {
            sessionId = latestSessionId;
            oldSessionParameters = currentParameters;
            currentParameters = sessionParameters;
        }
        if (oldSessionParameters != null && sessionId != null) {
            sessionLocalDataSource.removeSession(sessionId);
            closeSession(oldSessionParameters, closeSessionCallback);
            subscribersHolder.notifySessionParametersChanged();
        }
    }
}
