package com.inappstory.sdk.core.network.utils.headers;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;

public class AuthSessionIdHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.AUTH_SESSION_ID;
    }

    @Override
    public String getValue() {
        SessionDTO sessionDTO = IASCore.getInstance().sessionRepository.getSessionData();
        if (sessionDTO != null)
            return sessionDTO.getId();
        else
            return null;
    }
}
