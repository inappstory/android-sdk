package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.stories.api.models.RequestLocalParameters;

public interface OpenSessionCallback {
    void onSuccess(RequestLocalParameters sessionId);

    void onError();
}