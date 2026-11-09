package com.inappstory.sdk.stories.statistic;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.PhoneAppData;

public class StoryStatisticV2Task {
    @SerializedName(ModelFields.StoryStatisticV2Task.event)
    public String event;
    @SerializedName(ModelFields.StoryStatisticV2Task.timestamp)
    public Long timestamp;
    @SerializedName(ModelFields.StoryStatisticV2Task.userId)
    public String userId;
    @SerializedName(ModelFields.StoryStatisticV2Task.sessionId)
    public String sessionId;
    @SerializedName(ModelFields.StoryStatisticV2Task.isFake)
    public boolean isFake;
    @SerializedName(ModelFields.StoryStatisticV2Task.storyId)
    public String storyId;
    @SerializedName(ModelFields.StoryStatisticV2Task.feedId)
    public String feedId;
    @SerializedName(ModelFields.StoryStatisticV2Task.whence)
    public String whence;
    @SerializedName(ModelFields.StoryStatisticV2Task.target)
    public String target;
    @SerializedName(ModelFields.StoryStatisticV2Task.cause)
    public String cause;
    @SerializedName(ModelFields.StoryStatisticV2Task.slideIndex)
    public Integer slideIndex;
    @SerializedName(ModelFields.StoryStatisticV2Task.slideTotal)
    public Integer slideTotal;
    @SerializedName(ModelFields.StoryStatisticV2Task.durationMs)
    public Long durationMs;
    @SerializedName(ModelFields.StoryStatisticV2Task.value)
    public String value;
    @SerializedName(ModelFields.StoryStatisticV2Task.widgetId)
    public String widgetId;
    @SerializedName(ModelFields.StoryStatisticV2Task.widgetLabel)
    public String widgetLabel;
    @SerializedName(ModelFields.StoryStatisticV2Task.widgetValue)
    public String widgetValue;
    @SerializedName(ModelFields.StoryStatisticV2Task.widgetAnswer)
    public Integer widgetAnswer;
    @SerializedName(ModelFields.StoryStatisticV2Task.widgetAnswerLabel)
    public String widgetAnswerLabel;
    @SerializedName(ModelFields.StoryStatisticV2Task.widgetAnswerScore)
    public Integer widgetAnswerScore;
    @SerializedName(ModelFields.StoryStatisticV2Task.layoutIndex)
    public Integer layoutIndex;
    @SerializedName(ModelFields.StoryStatisticV2Task.mode)
    public Integer mode;
    @SerializedName(ModelFields.StoryStatisticV2Task.fullData)
    public String fullData;

}
