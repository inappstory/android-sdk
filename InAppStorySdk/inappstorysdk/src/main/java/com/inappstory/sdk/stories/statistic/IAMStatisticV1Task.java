package com.inappstory.sdk.stories.statistic;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.models.PhoneAppData;

public class IAMStatisticV1Task {
    public Integer iamId;
    public String event;
    @SerializedName("ei")
    public String eventId;
    @SerializedName("ii")
    public String iterationId;
    @SerializedName("si")
    public Integer slideIndex;
    @SerializedName("st")
    public Integer slideTotal;
    @SerializedName("d")
    public Long durationMs;
    @SerializedName("wi")
    public String widgetId;
    @SerializedName("wl")
    public String widgetLabel;
    @SerializedName("wv")
    public String widgetValue;
    @SerializedName("wa")
    public Integer widgetAnswer;
    @SerializedName("wal")
    public String widgetAnswerLabel;
    @SerializedName("was")
    public Integer widgetAnswerScore;

}
