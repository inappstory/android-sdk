package com.inappstory.sdk.refactoring.session.callbacks;

import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;
import com.inappstory.sdk.refactoring.session.errors.GetSessionError;

public interface GetSessionCallback {
    void success(SessionDTO session);
    void error(GetSessionError error);
}
