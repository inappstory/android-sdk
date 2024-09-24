package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

public abstract class UseIASCoreCallback {
    public abstract void use(@NonNull IASCoreImpl core) throws Exception;

    public void error() throws Exception {
    }
}
