package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

public abstract class UseIASCoreCallback {
    public abstract void use(@NonNull IASCore core) throws Exception;

    public void error() throws Exception {
    }
}
