package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.api.models.PreloadGameCenterData;

import org.json.JSONArray;

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
                PreloadGameCenterData data = new ToPreloadGameCenterData(pgcdList.optJSONObject(i)).parse();
                if (data == null) continue;
                preloadGameCenterDataList.add(data);
            }
            return preloadGameCenterDataList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "List of PreloadGameCenterData";
    }
}
