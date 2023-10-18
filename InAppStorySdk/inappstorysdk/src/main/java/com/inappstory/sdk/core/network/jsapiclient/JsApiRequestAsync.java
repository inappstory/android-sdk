package com.inappstory.sdk.core.network.jsapiclient;

import android.content.Context;

import com.inappstory.sdk.InAppStoryService;

import java.util.Map;
import java.util.concurrent.Callable;

public class JsApiRequestAsync implements Callable<JsApiResponse> {

    String method;
    String path;

    String baseUrl;
    Map<String, String> headers;
    Map<String, String> getParams;
    String body;
    String requestId;
    Context context;

    public JsApiRequestAsync(
            String method,
            String path,
            String baseUrl,
            Map<String, String> headers,
            Map<String, String> getParams,
            String body,
            String requestId,
            Context context
    ) {
        this.method = method;
        this.path = path;
        this.baseUrl = baseUrl;
        this.headers = headers;
        this.getParams = getParams;
        this.body = body;
        this.requestId = requestId;
        this.context = context;
    }

    @Override
    public JsApiResponse call() {
        try {
            JsApiResponse s = JsApiNetwork.sendRequest(
                    method,
                    path,
                    baseUrl,
                    headers,
                    getParams,
                    body,
                    requestId,
                    context
            );
            return s;
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            JsApiResponse response = new JsApiResponse();
            response.status = 12002;
            response.requestId = requestId;
            return response;
        }
    }
}