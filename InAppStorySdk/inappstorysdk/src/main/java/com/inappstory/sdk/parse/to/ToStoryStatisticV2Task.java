package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.ExtendedJsonObject;
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
        task.event = jsonObj.optStringNullable("event");
        task.timestamp = jsonObj.optLongNullable("ts");
        task.userId = jsonObj.optStringNullable("u");
        task.sessionId = jsonObj.optStringNullable("s");
        task.isFake = jsonObj.optBoolean("isFake");
        task.storyId = jsonObj.optStringNullable("i");
        task.feedId = jsonObj.optStringNullable("f");
        task.whence = jsonObj.optStringNullable("w");
        task.target = jsonObj.optStringNullable("t");
        task.cause = jsonObj.optStringNullable("c");
        task.value = jsonObj.optStringNullable("v");
        task.widgetId = jsonObj.optStringNullable("wi");
        task.widgetLabel = jsonObj.optStringNullable("wl");
        task.widgetValue = jsonObj.optStringNullable("wv");
        task.widgetAnswerLabel = jsonObj.optStringNullable("wal");
        task.fullData = jsonObj.optStringNullable("fullData");
        task.durationMs = jsonObj.optLongNullable("d");
        task.slideIndex = jsonObj.optIntNullable("si");
        task.slideTotal = jsonObj.optIntNullable("st");
        task.widgetAnswer = jsonObj.optIntNullable("wa");
        task.widgetAnswerScore = jsonObj.optIntNullable("was");
        task.layoutIndex = jsonObj.optIntNullable("li");
        task.mode = jsonObj.optIntNullable("m");
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
