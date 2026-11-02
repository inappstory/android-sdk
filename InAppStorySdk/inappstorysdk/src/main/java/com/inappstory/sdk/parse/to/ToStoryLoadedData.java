package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;
import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import org.json.JSONObject;


public final class ToStoryLoadedData extends ToModelParser<StoryLoadedData> {
    public ToStoryLoadedData(String value) {
        super(value);
    }



    @Override
    protected StoryLoadedData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            StoryLoadedData data = new StoryLoadedData();
            data.id = jsonObj.optInt("id");
            data.index = jsonObj.optInt("index");

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "StoryLoadedData";
    }
}
