package com.inappstory.sdk.stories.api.models.callbacks;

public interface OpenSessionCallback {
    void onSuccess(String sessionId);

    void onError();
}