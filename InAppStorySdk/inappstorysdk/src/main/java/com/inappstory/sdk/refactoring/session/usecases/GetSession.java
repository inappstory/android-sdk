package com.inappstory.sdk.refactoring.session.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;

public class GetSession {
    private final ISessionRepository sessionRepository;

    public GetSession(ISessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void invoke(ResultCallback<SessionDTO> callback) {
        this.sessionRepository.getSession(callback);
    }
}
