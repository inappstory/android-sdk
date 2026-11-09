package com.inappstory.sdk.stories.statistic;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.PhoneAppData;

public class IAMStatisticV1Task {
    @SerializedName(ModelFields.IAMStatisticV1Task.iamId)
    public Integer iamId;
    @SerializedName(ModelFields.IAMStatisticV1Task.event)
    public String event;
    @SerializedName(ModelFields.IAMStatisticV1Task.eventId)
    public String eventId;
    @SerializedName(ModelFields.IAMStatisticV1Task.iterationId)
    public String iterationId;
    @SerializedName(ModelFields.IAMStatisticV1Task.slideIndex)
    public Integer slideIndex;
    @SerializedName(ModelFields.IAMStatisticV1Task.layerIndex)
    public Integer layerIndex;
    @SerializedName(ModelFields.IAMStatisticV1Task.slideTotal)
    public Integer slideTotal;
    @SerializedName(ModelFields.IAMStatisticV1Task.durationMs)
    public Long durationMs;
    @SerializedName(ModelFields.IAMStatisticV1Task.slideAnalytics)
    public String slideAnalytics;
    @SerializedName(ModelFields.IAMStatisticV1Task.widgetId)
    public String widgetId;
    @SerializedName(ModelFields.IAMStatisticV1Task.widgetLabel)
    public String widgetLabel;
    @SerializedName(ModelFields.IAMStatisticV1Task.widgetValue)
    public String widgetValue;
    @SerializedName(ModelFields.IAMStatisticV1Task.value)
    public String value;
    @SerializedName(ModelFields.IAMStatisticV1Task.widgetAnswer)
    public Integer widgetAnswer;
    @SerializedName(ModelFields.IAMStatisticV1Task.widgetAnswerLabel)
    public String widgetAnswerLabel;
    @SerializedName(ModelFields.IAMStatisticV1Task.widgetAnswerScore)
    public Integer widgetAnswerScore;

}
