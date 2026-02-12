package com.inappstory.sdk.refactoring.session.callbacks;

import com.inappstory.sdk.refactoring.session.errors.CloseSessionError;

public interface CloseSessionCallback {
    void success();
    void error(CloseSessionError error);
}
