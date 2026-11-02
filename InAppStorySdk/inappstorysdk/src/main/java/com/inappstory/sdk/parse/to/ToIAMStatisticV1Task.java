package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.statistic.BannerStatisticV1Task;
import com.inappstory.sdk.stories.statistic.IAMStatisticV1Task;

import org.json.JSONObject;


public final class ToIAMStatisticV1Task extends ToModelParser<IAMStatisticV1Task> {
    public ToIAMStatisticV1Task(String value) {
        super(value);
    }



    @Override
    protected IAMStatisticV1Task parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            IAMStatisticV1Task task = new IAMStatisticV1Task();
            task.iamId = jsonObj.optIntNullable("iamId");
            task.event = jsonObj.optStringNullable("event");
            task.eventId = jsonObj.optStringNullable("ei");
            task.iterationId = jsonObj.optStringNullable("ii");
            task.slideIndex = jsonObj.optIntNullable("si");
            task.layerIndex = jsonObj.optIntNullable("li");
            task.slideTotal = jsonObj.optIntNullable("st");
            task.durationMs = jsonObj.optLongNullable("d");
            task.widgetId = jsonObj.optStringNullable("wi");
            task.widgetLabel = jsonObj.optStringNullable("wl");
            task.widgetValue = jsonObj.optStringNullable("wv");
            task.value = jsonObj.optStringNullable("v");
            task.slideAnalytics = jsonObj.optStringNullable("sa");
            task.widgetAnswerLabel = jsonObj.optStringNullable("wal");
            task.widgetAnswer = jsonObj.optIntNullable("wa");
            task.widgetAnswerScore = jsonObj.optIntNullable("was");

            return task;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "IAMStatisticV1Task";
    }
}
