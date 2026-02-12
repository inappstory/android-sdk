package com.inappstory.sdk.refactoring.session.callbacks;

import com.inappstory.sdk.refactoring.session.errors.UpdateSessionError;

public interface UpdateSessionCallback {
    void success();
    void error(UpdateSessionError error);
}
