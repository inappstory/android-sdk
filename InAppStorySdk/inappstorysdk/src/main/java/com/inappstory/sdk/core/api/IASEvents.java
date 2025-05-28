package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

public interface IASEvents {
    void useEvent(
            IASEventType type,
            @NonNull IASEventHandler iasEventHandler
    );

    void setEvent(
            IASEventType eventType,
            IASEvent event
    );

}
