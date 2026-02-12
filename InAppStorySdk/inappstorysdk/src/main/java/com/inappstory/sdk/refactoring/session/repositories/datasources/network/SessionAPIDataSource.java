package com.inappstory.sdk.refactoring.session.repositories.datasources.network;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.session.UniqueSessionParameters;
import com.inappstory.sdk.refactoring.session.callbacks.CloseSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.GetSessionCallback;
import com.inappstory.sdk.refactoring.session.callbacks.UpdateSessionCallback;
import com.inappstory.sdk.refactoring.session.repositories.datasources.local.SessionLocalDataSource;

public class SessionAPIDataSource {
    private final IASCore core;

    public SessionAPIDataSource(IASCore core) {
        this.core = core;
    }

    public void getSession(
            GetSessionCallback callback,
            UniqueSessionParameters sessionParameters
    ) {

    }

    public void updateSession(UpdateSessionCallback callback) {

    }

    public void closeSession(CloseSessionCallback callback) {

    }
}
