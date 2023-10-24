package com.inappstory.sdk.core.network.utils.headers;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;

public class XUserIdHeader implements Header {
    @Override
    public String getKey() {
        return HeadersKeys.USER_ID;
    }

    @Override
    public String getValue() {
        SessionDTO sessionDTO = IASCoreManager.getInstance().sessionRepository.getSessionData();
        if (sessionDTO != null) return sessionDTO.getUserId();
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null) {
            return manager.getUserId();
        }
        return null;
    }
}
