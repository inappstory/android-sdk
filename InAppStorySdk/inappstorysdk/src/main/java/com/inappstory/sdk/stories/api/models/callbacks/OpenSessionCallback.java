package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.network.models.RequestLocalParameters;

public interface OpenSessionCallback {
    void onSuccess(RequestLocalParameters sessionId);

    void onError();
}