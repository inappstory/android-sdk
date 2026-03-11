package com.inappstory.sdk.refactoring.session.data.mappers;

import com.inappstory.sdk.refactoring.core.utils.usecases.Mapper;
import com.inappstory.sdk.refactoring.session.data.local.SessionPlaceholderDTO;
import com.inappstory.sdk.refactoring.session.data.network.NSessionPlaceholder;

public class NPlaceholderToPlaceholderDTOMapper
        implements Mapper<NSessionPlaceholder, SessionPlaceholderDTO> {
    @Override
    public SessionPlaceholderDTO convert(NSessionPlaceholder obj) {
        return null;
    }
}
