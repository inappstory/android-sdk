package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;

import org.json.JSONObject;

public class FromExceptionLog extends ModelToStringParser<ExceptionLog> {
    public FromExceptionLog(ExceptionLog model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("timestamp", model.timestamp);
        object.put("id", model.id);
        object.put("session", model.session);
        object.put("file", model.file);
        object.put("message", model.message);
        object.put("stacktrace", model.stacktrace);
        object.put("line", model.line);
        object.put("cause", model.cause);
        return object;
    }
}
