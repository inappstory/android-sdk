package com.inappstory.sdk.core.utils;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.network.content.models.SessionAsset;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssetUrlsExtractor {
    private final IASCore core;

    public AssetUrlsExtractor(IASCore core) {
        this.core = core;
    }

    public Set<String> extract(IReaderContent readerContent) {
        List<SessionAsset> allAssets = core.assetsHolder().assets();
        Set<String> res = new HashSet<>();
        Map<String, String> layoutTemplateVariables = readerContent.layoutTemplateVariables();
        for (String variable : layoutTemplateVariables.values()) {
            for (SessionAsset sessionAsset : allAssets) {
                if (variable.contains(sessionAsset.replaceKey)) {
                    res.add(sessionAsset.url);
                }
            }
        }
        return res;
    }
}
