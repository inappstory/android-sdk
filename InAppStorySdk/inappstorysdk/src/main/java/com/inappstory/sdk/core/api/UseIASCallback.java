package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

public abstract class UseIASCallback<T extends IASCallback> {
    public abstract void use(@NonNull T callback);

    public void onDefault() {

    }
}
