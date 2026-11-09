package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.statistic.BannerStatisticV1Task;

import org.json.JSONObject;


public final class ToBannerStatisticV1Task extends ToModelParser<BannerStatisticV1Task> {
    public ToBannerStatisticV1Task(String value) {
        super(value);
    }



    @Override
    protected BannerStatisticV1Task parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            BannerStatisticV1Task task = new BannerStatisticV1Task();
            task.bannerId = jsonObj.optIntNullable(
                    ModelFields.BannerStatisticV1Task.bannerId
            );
            task.event = jsonObj.optStringNullable(
                    ModelFields.BannerStatisticV1Task.event
            );
            task.eventId = jsonObj.optStringNullable(
                    ModelFields.BannerStatisticV1Task.eventId
            );
            task.iterationId = jsonObj.optStringNullable(
                    ModelFields.BannerStatisticV1Task.iterationId
            );
            task.slideIndex = jsonObj.optIntNullable(
                    ModelFields.BannerStatisticV1Task.slideIndex
            );
            task.slideTotal = jsonObj.optIntNullable(
                    ModelFields.BannerStatisticV1Task.slideTotal
            );
            task.durationMs = jsonObj.optLongNullable(
                    ModelFields.BannerStatisticV1Task.durationMs
            );
            task.widgetId = jsonObj.optStringNullable(
                    ModelFields.BannerStatisticV1Task.widgetId
            );
            task.widgetLabel = jsonObj.optStringNullable(
                    ModelFields.BannerStatisticV1Task.widgetLabel
            );
            task.widgetValue = jsonObj.optStringNullable(
                    ModelFields.BannerStatisticV1Task.widgetValue
            );
            task.widgetAnswerLabel = jsonObj.optStringNullable(
                    ModelFields.BannerStatisticV1Task.widgetAnswerLabel
            );
            task.widgetAnswer = jsonObj.optIntNullable(
                    ModelFields.BannerStatisticV1Task.widgetAnswer
            );
            task.widgetAnswerScore = jsonObj.optIntNullable(
                    ModelFields.BannerStatisticV1Task.widgetAnswerScore
            );

            return task;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "BannerStatisticV1Task";
    }
}
