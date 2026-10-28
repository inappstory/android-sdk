package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.PreloadGameCenterData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public final class ToPreloadGameCenterDataList extends ToModelParser<List<PreloadGameCenterData>> {
    public ToPreloadGameCenterDataList(String value) {
        super(value);
    }

    @Override
    protected List<PreloadGameCenterData> parseString(@NonNull String value) {
        try {
            JSONArray pgcdList = new JSONArray(value);
            List<PreloadGameCenterData> preloadGameCenterDataList = new ArrayList<>();
            for (int i = 0; i < pgcdList.length(); i++) {
                preloadGameCenterDataList.add();
            }
            return preloadGameCenterDataList;
        } catch (Exception e) {
            return null;
        }
    }
}
