package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.network.jsapiclient.JsApiRequestConfig;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;

import org.json.JSONObject;


public final class ToExceptionLog extends ToModelParser<ExceptionLog> {
    public ToExceptionLog(String value) {
        super(value);
    }



    @Override
    protected ExceptionLog parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            ExceptionLog log = new ExceptionLog();
            log.id = jsonObj.optStringNullable(
                    ModelFields.ExceptionLog.id
            );
            log.session = jsonObj.optStringNullable(
                    ModelFields.ExceptionLog.session
            );
            log.stacktrace = jsonObj.optStringNullable(
                    ModelFields.ExceptionLog.stacktrace
            );
            log.cause = jsonObj.optStringNullable(
                    ModelFields.ExceptionLog.cause
            );
            log.file = jsonObj.optStringNullable(
                    ModelFields.ExceptionLog.file
            );
            log.message = jsonObj.optStringNullable(
                    ModelFields.ExceptionLog.message
            );
            log.line = jsonObj.optInt(
                    ModelFields.ExceptionLog.line
            );
            log.timestamp = jsonObj.optLong(
                    ModelFields.ExceptionLog.timestamp
            );

            return log;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "ExceptionLog";
    }
}
