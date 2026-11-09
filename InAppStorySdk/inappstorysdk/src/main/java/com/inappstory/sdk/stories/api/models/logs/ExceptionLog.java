package com.inappstory.sdk.stories.api.models.logs;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class ExceptionLog {
    @SerializedName(ModelFields.ExceptionLog.timestamp)
    public long timestamp;
    @SerializedName(ModelFields.ExceptionLog.id)
    public String id;
    @SerializedName(ModelFields.ExceptionLog.session)
    public String session;
    @SerializedName(ModelFields.ExceptionLog.message)
    public String message;
    @SerializedName(ModelFields.ExceptionLog.file)
    public String file;
    @SerializedName(ModelFields.ExceptionLog.line)
    public int line;
    @SerializedName(ModelFields.ExceptionLog.cause)
    public String cause;
    @SerializedName(ModelFields.ExceptionLog.stacktrace)
    public String stacktrace;
}
