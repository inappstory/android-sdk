package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.SerializedName;
import com.inappstory.sdk.stories.statistic.PhoneAppData;

public class StatisticTask {
    public String event;
    public PhoneAppData app;
    @SerializedName("ts")
    public Long timestamp;
    @SerializedName("u")
    public String userId;
    @SerializedName("s")
    public String sessionId;

    @SerializedName("i")
    public String storyId;
    @SerializedName("w")
    public String whence;
    @SerializedName("c")
    public String cause;
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
    @SerializedName("li")
    public Integer layoutIndex;
}
