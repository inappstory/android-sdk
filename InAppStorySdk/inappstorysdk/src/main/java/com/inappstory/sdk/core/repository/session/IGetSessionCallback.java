package com.inappstory.sdk.core.repository.session;

import com.inappstory.sdk.core.repository.session.dto.SessionDTO;

public interface IGetSessionCallback {
    void onSuccess(SessionDTO session);

    void onError();
}
