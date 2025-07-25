package com.inappstory.sdk.network.utils;


import android.util.Pair;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.network.models.ResponseWithRawData;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class RequestSender {
    public Response send(Request req, String requestId) throws Exception {
        ApiLogRequest requestLog = new ApiLogRequest();
        Pair<HttpURLConnection, Map<String, List<String>>> connectionWithProperties = new RequestConnection().build(req, requestId);
        HttpURLConnection connection = connectionWithProperties.first;
        requestLog.buildFromRequest(req, requestId);
        requestLog.setHeaders(connectionWithProperties.second);
        InAppStoryManager.sendApiRequestLog(requestLog);
        InAppStoryManager.showDLog("InAppStory_Network", requestLog.getCurl());
        ResponseWithRawData responseWithRawData = new RawResponseFromConnection().get(connection, requestId);

        ApiLogResponse responseLog = new ApiLogResponse();
        responseLog.buildFromRawResponse(responseWithRawData, requestId);
        InAppStoryManager.sendApiResponseLog(responseLog);

        connection.disconnect();
        return responseWithRawData.response;
    }
}
