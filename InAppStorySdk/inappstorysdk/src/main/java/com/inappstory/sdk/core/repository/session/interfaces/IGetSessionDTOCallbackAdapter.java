package com.inappstory.sdk.core.repository.session.interfaces;

import com.inappstory.sdk.core.repository.session.dto.SessionDTO;

public abstract class IGetSessionDTOCallbackAdapter implements IGetSessionCallback<SessionDTO> {

    private final NetworkErrorCallback callback;
    public IGetSessionDTOCallbackAdapter(NetworkErrorCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onError() {
        callback.onError();
    }
}
