package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

public interface IASCallbacks {
    void useCallback(
            IASCallbackType type,
            @NonNull UseIASCallback useIASCallback
    );

    void setCallback(
            IASCallbackType type,
            IASCallback useIASCallback
    );

}
