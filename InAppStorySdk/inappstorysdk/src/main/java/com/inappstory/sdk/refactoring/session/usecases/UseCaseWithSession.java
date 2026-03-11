package com.inappstory.sdk.refactoring.session.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.Error;
import com.inappstory.sdk.refactoring.core.utils.results.NoSessionError;
import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.core.utils.usecases.UseCase;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;
import com.inappstory.sdk.refactoring.session.repositories.ISessionRepository;
import com.inappstory.sdk.refactoring.session.usecases.GetSession;

public abstract class UseCaseWithSession<T> implements UseCase<T> {
    private final GetSession getSessionUC;

    protected UseCaseWithSession(ISessionRepository sessionRepository) {
        getSessionUC = new GetSession(sessionRepository);
    }

    protected abstract void invokeWithSession(ResultCallback<T> callback);

    @Override
    public void invoke(final ResultCallback<T> callback) {
        getSessionUC.invoke(
                new ResultCallback<SessionDTO>() {
                    @Override
                    public void success(SessionDTO result) {
                        invokeWithSession(callback);
                    }

                    @Override
                    public void error(Error<SessionDTO> result) {
                        callback.error(new NoSessionError<>());
                    }
                }
        );
    }
}
