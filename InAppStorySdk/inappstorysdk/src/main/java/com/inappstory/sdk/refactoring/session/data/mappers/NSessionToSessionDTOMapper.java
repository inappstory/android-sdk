package com.inappstory.sdk.refactoring.session.data.mappers;

import com.inappstory.sdk.refactoring.core.utils.usecases.Mapper;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;
import com.inappstory.sdk.refactoring.session.data.network.NSession;

public class NSessionToSessionDTOMapper
        implements Mapper<NSession, SessionDTO> {
    @Override
    public SessionDTO convert(NSession obj) {
        return null;
    }
}
