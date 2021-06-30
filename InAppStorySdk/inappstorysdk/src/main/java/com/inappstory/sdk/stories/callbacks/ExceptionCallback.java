package com.inappstory.sdk.stories.callbacks;

public interface ExceptionCallback {
    /**
     * use to customize uncaught exception behaviour
     * @param throwable (throwable)
     */
    void onException(Throwable throwable);
}
