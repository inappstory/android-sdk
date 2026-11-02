package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;
import com.inappstory.sdk.stories.api.models.dialogstructure.DialogStructure;

import org.json.JSONObject;


public final class ToUpdateTimelineData extends ToModelParser<UpdateTimelineData> {
    public ToUpdateTimelineData(String value) {
        super(value);
    }



    @Override
    protected UpdateTimelineData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            UpdateTimelineData updateTimelineData = new UpdateTimelineData();
            updateTimelineData.action = jsonObj.optStringNullable("action");
            updateTimelineData.currentTime = jsonObj.optLong("currentTime");
            updateTimelineData.duration = jsonObj.optLong("duration");
            updateTimelineData.showLoader = jsonObj.optBoolean("showLoader");
            updateTimelineData.showError = jsonObj.optBoolean("showError");
            updateTimelineData.slideIndex = jsonObj.optInt("slideIndex");
            return updateTimelineData;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "UpdateTimelineData";
    }
}
