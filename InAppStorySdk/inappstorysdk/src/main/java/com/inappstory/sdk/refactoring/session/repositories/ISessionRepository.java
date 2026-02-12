package com.inappstory.sdk.refactoring.session.repositories;

import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;
import com.inappstory.sdk.refactoring.session.callbacks.CloseSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.GetSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.UpdateSessionCallback;

public interface ISessionRepository {
    void getSession(GetSessionCallback callback);

    void updateSession(UpdateSessionCallback callback);

    void closeSession(CloseSessionCallback callback);

    void setCurrentSessionParameters(
            UniqueSessionParameters sessionParameters,
            CloseSessionCallback callback
    );
}
