package com.inappstory.sdk.refactoring.session.data.mappers;

import com.inappstory.sdk.refactoring.core.utils.usecases.Mapper;
import com.inappstory.sdk.refactoring.session.data.local.SessionAssetDTO;
import com.inappstory.sdk.refactoring.session.data.local.SessionDTO;
import com.inappstory.sdk.refactoring.session.data.local.SessionPlaceholderDTO;
import com.inappstory.sdk.refactoring.session.data.network.NSession;
import com.inappstory.sdk.refactoring.session.data.network.NSessionAsset;
import com.inappstory.sdk.refactoring.session.data.network.NSessionPlaceholder;

import java.util.ArrayList;
import java.util.List;

public class NSessionToSessionDTOMapper
        implements Mapper<NSession, SessionDTO> {
    @Override
    public SessionDTO convert(NSession obj) {
        if (obj.sessionId == null) return null;
        SessionDTO sessionDTO = new SessionDTO(
                obj.sessionId.id,
                obj.isAllowUgc,
                obj.preloadGame,
                obj.previewAspectRatio,
                obj.isAllowProfiling,
                obj.isAllowStatV1,
                obj.isAllowStatV2,
                obj.isAllowCrash
        );
        List<SessionPlaceholderDTO> imagePlaceholders = new ArrayList<>();
        List<SessionPlaceholderDTO> placeholders = new ArrayList<>();
        List<SessionAssetDTO> assets = new ArrayList<>();
        NPlaceholderToPlaceholderDTOMapper placeholderDTOMapper = new NPlaceholderToPlaceholderDTOMapper();
        NAssetToAssetDTOMapper assetDTOMapper = new NAssetToAssetDTOMapper();
        if (obj.imagePlaceholders != null)
            for (NSessionPlaceholder placeholder : obj.imagePlaceholders) {
                imagePlaceholders.add(placeholderDTOMapper.convert(placeholder));
            }
        if (obj.sessionAssets != null) {
            for (NSessionAsset sessionAsset : obj.sessionAssets) {
                assets.add(assetDTOMapper.convert(sessionAsset));
            }
        }
        if (obj.placeholders != null)
            for (NSessionPlaceholder placeholder : obj.placeholders) {
                placeholders.add(placeholderDTOMapper.convert(placeholder));
            }
        sessionDTO.imagePlaceholders(imagePlaceholders);
        sessionDTO.sessionAssets(assets);
        sessionDTO.placeholders(placeholders);
        return sessionDTO;
    }
}
