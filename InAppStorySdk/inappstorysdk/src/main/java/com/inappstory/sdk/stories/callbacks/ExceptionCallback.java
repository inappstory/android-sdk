package com.inappstory.sdk.stories.callbacks;

import com.inappstory.sdk.core.api.IASCallback;

public interface ExceptionCallback extends IASCallback {
    /**
     * use to customize uncaught exception behaviour
     * @param throwable (throwable)
     */
    void onException(Throwable throwable);
}
