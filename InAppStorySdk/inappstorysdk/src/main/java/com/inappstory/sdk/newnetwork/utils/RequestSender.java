package com.inappstory.sdk.newnetwork.utils;


import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.newnetwork.models.Request;
import com.inappstory.sdk.newnetwork.models.Response;
import com.inappstory.sdk.newnetwork.models.ResponseWithRawData;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;

import java.net.HttpURLConnection;

public class RequestSender {
    public Response send(Request req, String requestId) throws Exception {
        ApiLogRequest requestLog = new ApiLogRequest();
        HttpURLConnection connection = new RequestConnection().build(req, requestId);

        requestLog.buildFromRequest(req, requestId);
        requestLog.setHeaders(connection.getRequestProperties());
        InAppStoryManager.sendApiRequestLog(requestLog);

        ResponseWithRawData responseWithRawData = new RawResponseFromConnection().get(connection, requestId);

        ApiLogResponse responseLog = new ApiLogResponse();
        responseLog.buildFromRawResponse(responseWithRawData, requestId);
        InAppStoryManager.sendApiResponseLog(responseLog);

        connection.disconnect();
        return responseWithRawData.response;
    }
}
