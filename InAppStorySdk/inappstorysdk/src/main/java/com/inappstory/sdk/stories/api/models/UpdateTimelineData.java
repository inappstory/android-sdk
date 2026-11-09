package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class UpdateTimelineData {
    @SerializedName(ModelFields.UpdateTimelineData.action)
    public String action;
    @SerializedName(ModelFields.UpdateTimelineData.currentTime)
    public long currentTime;
    @SerializedName(ModelFields.UpdateTimelineData.duration)
    public long duration;
    @SerializedName(ModelFields.UpdateTimelineData.slideIndex)
    public int slideIndex;
    @SerializedName(ModelFields.UpdateTimelineData.showLoader)
    public boolean showLoader;
    @SerializedName(ModelFields.UpdateTimelineData.showError)
    public boolean showError;

    @Override
    public String toString() {
        return "UpdateTimelineData{" +
                "action='" + action + '\'' +
                ", currentTime=" + currentTime +
                ", duration=" + duration +
                ", slideIndex=" + slideIndex +
                ", showLoader=" + showLoader +
                ", showError=" + showError +
                '}';
    }
}
