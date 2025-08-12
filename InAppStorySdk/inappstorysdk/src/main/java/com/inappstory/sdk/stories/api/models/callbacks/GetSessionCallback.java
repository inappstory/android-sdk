package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.network.models.RequestLocalParameters;

public interface GetSessionCallback {
    void onSuccess(RequestLocalParameters sessionId);

    void onError();
}