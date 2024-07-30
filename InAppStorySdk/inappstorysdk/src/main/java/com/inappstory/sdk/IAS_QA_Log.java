package com.inappstory.sdk;

import static com.inappstory.sdk.InAppStoryManager.IAS_DEBUG_API;

import android.annotation.SuppressLint;

import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.api.models.logs.WebConsoleLog;

@SuppressLint(IAS_DEBUG_API)
public interface IAS_QA_Log {
    void getApiRequestLog(ApiLogRequest request);

    void getApiResponseLog(ApiLogResponse response);

    void getApiRequestResponseLog(ApiLogRequest request,
                                  ApiLogResponse response);

    void getExceptionLog(ExceptionLog exceptionLog);

    void getWebConsoleLog(WebConsoleLog webConsoleLog);
}