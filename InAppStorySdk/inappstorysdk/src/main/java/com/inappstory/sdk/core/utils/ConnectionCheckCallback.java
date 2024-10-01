package com.inappstory.sdk.core.utils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;

public abstract class ConnectionCheckCallback {
    public ConnectionCheckCallback(IASCore core) {
        this.core = core;
    }

    private final IASCore core;

    public abstract void success();

    void error() {
        core.callbacksAPI().useCallback(IASCallbackType.ERROR,
                new UseIASCallback<ErrorCallback>() {
                    @Override
                    public void use(@NonNull ErrorCallback callback) {
                        callback.noConnection();
                    }
                }
        );
    }
}
