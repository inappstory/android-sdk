package com.inappstory.sdk.refactoring.session.repositories;

import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;

public interface ISessionRepository {
    void getSession(
            ResultCallback<SessionDTO> closeSessionCallback
    );

    void updateSession(
            ResultCallback<Void> updateSessionCallback);

    void closeSession(
            ResultCallback<Void> closeSessionCallback
    );

    void setCurrentSessionParameters(
            UniqueSessionParameters sessionParameters,
            ResultCallback<Void> closeSessionCallback
    );
}
