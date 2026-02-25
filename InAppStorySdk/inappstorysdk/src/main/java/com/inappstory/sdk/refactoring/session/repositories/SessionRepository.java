package com.inappstory.sdk.refactoring.session.repositories;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;
import com.inappstory.sdk.refactoring.session.callbacks.CloseSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.GetSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.UpdateSessionCallback;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;
import com.inappstory.sdk.refactoring.session.repositories.datasources.SessionLocalDataSource;
import com.inappstory.sdk.refactoring.session.repositories.datasources.SessionAPIDataSource;

import java.util.List;
import java.util.Map;

public class SessionRepository implements ISessionRepository {
    private final SessionAPIDataSource sessionAPIDataSource;
    private final SessionLocalDataSource sessionLocalDataSource;
    private final Object core;
    private final Object sessionParametersLock = new Object();
    private String latestSessionId;
    private Map<UniqueSessionParameters, List<GetSessionCallback>> callbacks;
    private UniqueSessionParameters currentParameters = null;

    public SessionRepository(IASCore core) {
        this.core = core;
        sessionAPIDataSource = new SessionAPIDataSource(core);
        sessionLocalDataSource = new SessionLocalDataSource();
    }

    @Override
    public void getSession(ResultCallback<SessionDTO> getSessionCallback) {

    }

    @Override
    public void updateSession(ResultCallback<Void> updateSessionCallback) {
        UniqueSessionParameters tempSessionParameters;
        synchronized (sessionParametersLock) {
            tempSessionParameters = currentParameters;
        }
        if (tempSessionParameters == null) return;
        sessionAPIDataSource.updateSession(callback, tempSessionParameters);
    }

    @Override
    public void closeSession(ResultCallback<Void> closeSessionCallback) {

    }

    @Override
    public void setCurrentSessionParameters(
            UniqueSessionParameters sessionParameters,
            ResultCallback<Void> closeSessionCallback
    ) {
        UniqueSessionParameters tempSessionParameters;
        synchronized (sessionParametersLock) {
            tempSessionParameters = currentParameters;
            currentParameters = sessionParameters;
        }
        if (tempSessionParameters == null) return;
        sessionLocalDataSource.removeSession(latestSessionId);
        sessionAPIDataSource.closeSession(callback, latestSessionId, tempSessionParameters);
    }
}
