package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            task.iamId = jsonObj.optIntNullable(
                    ModelFields.IAMStatisticV1Task.iamId
            );
            task.event = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.event
            );
            task.eventId = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.eventId
            );
            task.iterationId = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.iterationId
            );
            task.slideIndex = jsonObj.optIntNullable(
                    ModelFields.IAMStatisticV1Task.slideIndex
            );
            task.layerIndex = jsonObj.optIntNullable(
                    ModelFields.IAMStatisticV1Task.layerIndex
            );
            task.slideTotal = jsonObj.optIntNullable(
                    ModelFields.IAMStatisticV1Task.slideTotal
            );
            task.durationMs = jsonObj.optLongNullable(
                    ModelFields.IAMStatisticV1Task.durationMs
            );
            task.widgetId = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.widgetId
            );
            task.widgetLabel = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.widgetLabel
            );
            task.widgetValue = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.widgetValue
            );
            task.value = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.value
            );
            task.slideAnalytics = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.slideAnalytics
            );
            task.widgetAnswerLabel = jsonObj.optStringNullable(
                    ModelFields.IAMStatisticV1Task.widgetAnswerLabel
            );
            task.widgetAnswer = jsonObj.optIntNullable(
                    ModelFields.IAMStatisticV1Task.widgetAnswer
            );
            task.widgetAnswerScore = jsonObj.optIntNullable(
                    ModelFields.IAMStatisticV1Task.widgetAnswerScore
            );

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
