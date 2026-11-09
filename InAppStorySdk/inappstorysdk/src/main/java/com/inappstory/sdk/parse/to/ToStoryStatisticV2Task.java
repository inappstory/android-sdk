package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.statistic.IAMStatisticV1Task;
import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import org.json.JSONObject;


public final class ToStoryStatisticV2Task extends ToModelParser<StoryStatisticV2Task> {
    public ToStoryStatisticV2Task(String value) {
        super(value);
    }

    public ToStoryStatisticV2Task(JSONObject value) {
        super(value);
    }

    private StoryStatisticV2Task parseWithException(ExtendedJsonObject jsonObj) throws Exception {
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.event
        );
        task.timestamp = jsonObj.optLongNullable(
                ModelFields.StoryStatisticV2Task.timestamp
        );
        task.userId = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.userId
        );
        task.sessionId = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.sessionId
        );
        task.isFake = jsonObj.optBoolean(
                ModelFields.StoryStatisticV2Task.isFake
        );
        task.storyId = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.storyId
        );
        task.feedId = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.feedId
        );
        task.whence = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.whence
        );
        task.target = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.target
        );
        task.cause = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.cause
        );
        task.value = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.value
        );
        task.widgetId = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.widgetId
        );
        task.widgetLabel = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.widgetLabel
        );
        task.widgetValue = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.widgetValue
        );
        task.widgetAnswerLabel = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.widgetAnswerLabel
        );
        task.fullData = jsonObj.optStringNullable(
                ModelFields.StoryStatisticV2Task.fullData
        );
        task.durationMs = jsonObj.optLongNullable(
                ModelFields.StoryStatisticV2Task.durationMs
        );
        task.slideIndex = jsonObj.optIntNullable(
                ModelFields.StoryStatisticV2Task.slideIndex
        );
        task.slideTotal = jsonObj.optIntNullable(
                ModelFields.StoryStatisticV2Task.slideTotal
        );
        task.widgetAnswer = jsonObj.optIntNullable(
                ModelFields.StoryStatisticV2Task.widgetAnswer
        );
        task.widgetAnswerScore = jsonObj.optIntNullable(
                ModelFields.StoryStatisticV2Task.widgetAnswerScore
        );
        task.layoutIndex = jsonObj.optIntNullable(
                ModelFields.StoryStatisticV2Task.layoutIndex
        );
        task.mode = jsonObj.optIntNullable(
                ModelFields.StoryStatisticV2Task.mode
        );
        return task;
    }

    @Override
    protected StoryStatisticV2Task parseObject(JSONObject jsonObject) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(jsonObject);
            return parseWithException(jsonObj);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected StoryStatisticV2Task parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            return parseWithException(jsonObj);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "StoryStatisticV2Task";
    }
}
