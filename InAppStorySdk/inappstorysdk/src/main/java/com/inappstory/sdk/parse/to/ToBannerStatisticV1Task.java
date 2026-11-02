package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
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
            task.bannerId = jsonObj.optIntNullable("bannerId");
            task.event = jsonObj.optStringNullable("event");
            task.eventId = jsonObj.optStringNullable("ei");
            task.iterationId = jsonObj.optStringNullable("ii");
            task.slideIndex = jsonObj.optIntNullable("si");
            task.slideTotal = jsonObj.optIntNullable("st");
            task.durationMs = jsonObj.optLongNullable("d");
            task.widgetId = jsonObj.optStringNullable("wi");
            task.widgetLabel = jsonObj.optStringNullable("wl");
            task.widgetValue = jsonObj.optStringNullable("wv");
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
        return "BannerStatisticV1Task";
    }
}
