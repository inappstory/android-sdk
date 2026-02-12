package com.inappstory.sdk.refactoring.session.repositories;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;
import com.inappstory.sdk.refactoring.session.callbacks.CloseSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.GetSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.UpdateSessionCallback;
import com.inappstory.sdk.refactoring.session.repositories.datasources.local.SessionLocalDataSource;
import com.inappstory.sdk.refactoring.session.repositories.datasources.network.SessionAPIDataSource;

import java.util.List;
import java.util.Map;

public class SessionRepository implements ISessionRepository {
    private final SessionAPIDataSource sessionAPIDataSource;
    private final SessionLocalDataSource sessionLocalDataSource;
    private final Object core;
    private final Object sessionParametersLock = new Object();
    private String latestSessionId;
    private Map<UniqueSessionParameters, List<GetSessionCallback>> callbacks;

    public SessionRepository(IASCore core) {
        this.core = core;
        sessionAPIDataSource = new SessionAPIDataSource(core);
        sessionLocalDataSource = new SessionLocalDataSource();
    }

    @Override
    public void getSession(GetSessionCallback callback) {

    }

    @Override
    public void updateSession(UpdateSessionCallback callback) {

    }

    @Override
    public void closeSession(CloseSessionCallback callback) {

    }

    @Override
    public void setCurrentSessionParameters(
            UniqueSessionParameters sessionParameters,
            CloseSessionCallback callback
    ) {
        sessionLocalDataSource.removeSession(latestSessionId);
        sessionAPIDataSource.closeSession(callback);
    }
}
