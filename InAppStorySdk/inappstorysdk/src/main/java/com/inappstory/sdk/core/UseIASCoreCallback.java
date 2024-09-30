package com.inappstory.sdk.core;

import androidx.annotation.NonNull;


public abstract class UseIASCoreCallback {
    public abstract void use(@NonNull IASCore core);

    public void error() {
    }
}
