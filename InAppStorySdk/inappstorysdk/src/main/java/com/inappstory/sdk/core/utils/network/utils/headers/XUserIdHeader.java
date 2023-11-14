package com.inappstory.sdk.core.utils.network.utils.headers;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;

public class XUserIdHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.USER_ID;
    }

    @Override
    public String getValue() {
        SessionDTO sessionDTO = IASCore.getInstance().sessionRepository.getSessionData();
        if (sessionDTO != null) return sessionDTO.getUserId();
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null) {
            return manager.getUserId();
        }
        return null;
    }
}
