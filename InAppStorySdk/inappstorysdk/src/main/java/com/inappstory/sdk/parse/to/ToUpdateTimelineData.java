package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            updateTimelineData.action = jsonObj.optStringNullable(
                    ModelFields.UpdateTimelineData.action
            );
            updateTimelineData.currentTime = jsonObj.optLong(
                    ModelFields.UpdateTimelineData.currentTime
            );
            updateTimelineData.duration = jsonObj.optLong(
                    ModelFields.UpdateTimelineData.duration
            );
            updateTimelineData.showLoader = jsonObj.optBoolean(
                    ModelFields.UpdateTimelineData.showLoader
            );
            updateTimelineData.showError = jsonObj.optBoolean(
                    ModelFields.UpdateTimelineData.showError
            );
            updateTimelineData.slideIndex = jsonObj.optInt(
                    ModelFields.UpdateTimelineData.slideIndex
            );
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
