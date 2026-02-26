package com.inappstory.sdk.refactoring.session.usecases;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;

public class CloseSession {
    private final ISessionRepository sessionRepository;
    private final UniqueSessionParameters sessionParameters;

    public CloseSession(ISessionRepository sessionRepository, UniqueSessionParameters sessionParameters) {
        this.sessionRepository = sessionRepository;
        this.sessionParameters = sessionParameters;
    }

    public void invoke(ResultCallback<Void> callback) {
        this.sessionRepository.closeSession(sessionParameters, callback);
    }
}
