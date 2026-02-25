package com.inappstory.sdk.refactoring.session.data.mappers;

import com.inappstory.sdk.refactoring.core.utils.models.Mapper;
import com.inappstory.sdk.refactoring.session.data.local.SessionAssetDTO;
import com.inappstory.sdk.refactoring.session.data.network.NSessionAsset;

public class NAssetToAssetDTOMapper implements Mapper<NSessionAsset, SessionAssetDTO> {
    @Override
    public SessionAssetDTO convert(NSessionAsset obj) {
        return null;
    }
}
