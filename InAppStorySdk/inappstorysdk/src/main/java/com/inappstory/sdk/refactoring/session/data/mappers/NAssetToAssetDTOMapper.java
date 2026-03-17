package com.inappstory.sdk.refactoring.session.data.mappers;

import com.inappstory.sdk.refactoring.core.utils.usecases.Mapper;
import com.inappstory.sdk.refactoring.session.data.local.SessionAssetDTO;
import com.inappstory.sdk.refactoring.session.data.network.NSessionAsset;

public class NAssetToAssetDTOMapper implements Mapper<NSessionAsset, SessionAssetDTO> {
    @Override
    public SessionAssetDTO convert(NSessionAsset obj) {
        if (obj == null) return null;
        SessionAssetDTO sessionAssetDTO = new SessionAssetDTO();
        sessionAssetDTO.url = obj.url;
        sessionAssetDTO.filename = obj.filename;
        sessionAssetDTO.size = obj.size;
        sessionAssetDTO.sha1 = obj.sha1;
        sessionAssetDTO.type = obj.type;
        sessionAssetDTO.mimeType = obj.mimeType;
        sessionAssetDTO.replaceKey = obj.replaceKey;
        return sessionAssetDTO;
    }
}
