package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.network.jsapiclient.JsApiRequestConfig;
import com.inappstory.sdk.parse.ExtendedJsonObject;
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
            log.id = jsonObj.optStringNullable("id");
            log.session = jsonObj.optStringNullable("session");
            log.stacktrace = jsonObj.optStringNullable("stacktrace");
            log.cause = jsonObj.optStringNullable("cause");
            log.file = jsonObj.optStringNullable("file");
            log.message = jsonObj.optStringNullable("message");
            log.line = jsonObj.optInt("line");
            log.timestamp = jsonObj.optLong("timestamp");

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
