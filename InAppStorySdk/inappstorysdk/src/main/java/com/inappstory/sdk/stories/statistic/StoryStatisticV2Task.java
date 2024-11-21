package com.inappstory.sdk.stories.statistic;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.models.PhoneAppData;

public class StoryStatisticV2Task {
    public String event;
    public PhoneAppData app;
    @SerializedName("ts")
    public Long timestamp;
    @SerializedName("u")
    public String userId;
    @SerializedName("s")
    public String sessionId;
    public boolean isFake;
    @SerializedName("i")
    public String storyId;
    @SerializedName("f")
    public String feedId;
    @SerializedName("w")
    public String whence;
    @SerializedName("t")
    public String target;
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
    @SerializedName("m")
    public Integer mode;

}
