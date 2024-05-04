package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequestHeader;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;

import java.util.HashMap;
import java.util.UUID;

public class GenerateDownloadLog {
    ApiLogRequest requestLog;
    ApiLogResponse responseLog;

    public void generateRequestLog(String url) {
        String requestId = UUID.randomUUID().toString();
        requestLog = new ApiLogRequest();
        requestLog.method = "GET";
        requestLog.url = url;
        requestLog.isStatic = true;
        requestLog.id = requestId;
    }

    public void sendRequestLog() {
        InAppStoryManager.sendApiRequestLog(requestLog);
    }

    public void sendResponseLog() {
        InAppStoryManager.sendApiResponseLog(responseLog);
    }

    public void sendRequestResponseLog() {
        InAppStoryManager.sendApiRequestResponseLog(requestLog, responseLog);
    }

    public ApiLogResponse generateResponseLog(boolean fromCache, String filePath) {
        responseLog = new ApiLogResponse();
        HashMap<String, String> headers = new HashMap<>();
        responseLog.id = requestLog.id;
        if (fromCache) {
            headers.put("From Cache", "true");
            responseLog.generateFile(200, filePath, headers);
        } else {
            responseLog.responseHeaders.add(new ApiLogRequestHeader("From Cache", "false"));
        }
        return responseLog;
    }
}
