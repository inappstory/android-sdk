package com.inappstory.sdk.core.repository.session.interfaces;

import android.content.Context;

import com.inappstory.sdk.core.models.api.SessionResponse;

public interface IOpenSession {
    void open(
            Context context,
            String userId,
            final IGetSessionCallback<SessionResponse> callback
    );
}
