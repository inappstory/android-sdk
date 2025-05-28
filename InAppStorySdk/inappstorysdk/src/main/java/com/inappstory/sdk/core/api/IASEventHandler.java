package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

public abstract class IASEventHandler<T extends IASEvent> {
    public abstract void handleEvent(@NonNull T IASEvent);

    public void onDefault() {

    }
}
