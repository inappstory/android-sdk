package com.inappstory.sdk.core.repository.session.interfaces;

import com.inappstory.sdk.core.repository.session.dto.SessionDTO;

import java.util.List;

public interface ICloseSession {
    void close(
            SessionDTO sessionDTO,
            List<List<Object>> stat
    );
}
