package com.inappstory.sdk.stories.statistic;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class BannerStatisticV1Task {
    @SerializedName(ModelFields.BannerStatisticV1Task.bannerId)
    public Integer bannerId;
    @SerializedName(ModelFields.BannerStatisticV1Task.event)
    public String event;
    @SerializedName(ModelFields.BannerStatisticV1Task.eventId)
    public String eventId;
    @SerializedName(ModelFields.BannerStatisticV1Task.iterationId)
    public String iterationId;
    @SerializedName(ModelFields.BannerStatisticV1Task.slideIndex)
    public Integer slideIndex;
    @SerializedName(ModelFields.BannerStatisticV1Task.slideTotal)
    public Integer slideTotal;
    @SerializedName(ModelFields.BannerStatisticV1Task.durationMs)
    public Long durationMs;
    @SerializedName(ModelFields.BannerStatisticV1Task.widgetId)
    public String widgetId;
    @SerializedName(ModelFields.BannerStatisticV1Task.widgetLabel)
    public String widgetLabel;
    @SerializedName(ModelFields.BannerStatisticV1Task.widgetValue)
    public String widgetValue;
    @SerializedName(ModelFields.BannerStatisticV1Task.widgetAnswer)
    public Integer widgetAnswer;
    @SerializedName(ModelFields.BannerStatisticV1Task.widgetAnswerLabel)
    public String widgetAnswerLabel;
    @SerializedName(ModelFields.BannerStatisticV1Task.widgetAnswerScore)
    public Integer widgetAnswerScore;

}
