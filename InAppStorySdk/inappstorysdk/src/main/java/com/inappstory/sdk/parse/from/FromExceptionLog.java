package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;

import org.json.JSONObject;

public class FromExceptionLog extends ModelToStringParser<ExceptionLog> {
    public FromExceptionLog(ExceptionLog model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put(ModelFields.ExceptionLog.timestamp, model.timestamp);
        object.put(ModelFields.ExceptionLog.id, model.id);
        object.put(ModelFields.ExceptionLog.session, model.session);
        object.put(ModelFields.ExceptionLog.file, model.file);
        object.put(ModelFields.ExceptionLog.message, model.message);
        object.put(ModelFields.ExceptionLog.stacktrace, model.stacktrace);
        object.put(ModelFields.ExceptionLog.line, model.line);
        object.put(ModelFields.ExceptionLog.cause, model.cause);
        return object;
    }
}
