package com.inappstory.sdk.core.repository.session.interfaces;

import com.inappstory.sdk.core.repository.session.dto.SessionDTO;

import java.util.List;

public interface IUpdateSession {
    void update(
            SessionDTO sessionDTO,
            List<List<Object>> stat,
            IUpdateSessionCallback callback
    );
}
