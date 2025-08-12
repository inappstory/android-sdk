package com.inappstory.sdk.network.jsapiclient;

import static com.inappstory.sdk.network.JsonParser.toMap;

import android.content.Context;
import android.util.Pair;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.callbacks.NoTypeNetworkCallback;
import com.inappstory.sdk.network.constants.HttpMethods;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.network.utils.headers.CustomHeader;
import com.inappstory.sdk.network.utils.headers.Header;
import com.inappstory.sdk.stories.api.models.callbacks.GetSessionCallback;
import com.inappstory.sdk.stories.utils.TaskRunner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.text.UStringsKt;

public class JsApiClient {
    Context context;
    private final IASCore core;

    String baseUrl;

    public JsApiClient(IASCore core, Context context, String baseUrl) {
        this.core = core;
        this.context = context;
        this.baseUrl = baseUrl;
    }

    public void sendApiRequest(String data, JsApiResponseCallback callback) {
        JsApiRequestConfig config = JsonParser.fromJson(data, JsApiRequestConfig.class);
        Map<String, String> headers = null;
        if (config.headers != null && !config.headers.isEmpty()) {
            headers = toMap(config.headers);
        }
        Map<String, String> getParams = null;
        if (config.params != null && !config.params.isEmpty()) {
            getParams = toMap(config.params);
        }
        checkSessionAndSendRequest(config.method, config.url, headers, getParams,
                config.data, config.id, config.cb, config.profilingKey, callback);
    }

    void checkSessionAndSendRequest(
            final String method,
            final String path,
            final Map<String, String> headers,
            final Map<String, String> getParams,
            final String body,
            final String requestId,
            final String cb,
            final String profilingKey,
            final JsApiResponseCallback callback
    ) {
        core.sessionManager().useOrOpenSession(
                new GetSessionCallback() {
                    @Override
                    public void onSuccess(RequestLocalParameters requestLocalParameters) {
                        sendRequest(
                                method,
                                path,
                                baseUrl,
                                headers,
                                getParams,
                                body,
                                requestId,
                                cb,
                                profilingKey,
                                callback
                        );
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
    }

    String escapeString(String raw) {
        String escaped = JSONObject.quote(raw)
                .replaceFirst("^\"(.*)\"$", "$1")
                .replaceAll("\n", " ")
                .replaceAll("\r", " ");
        return escaped;
    }

    private Request generateRequest(
            String method,
            String path,
            String baseUrl,
            Map<String, String> headers,
            Map<String, String> getParams,
            String body
    ) {

        Request.Builder requestBuilder = new Request.Builder();
        boolean hasBody = !method.equals(HttpMethods.GET)
                && !method.equals(HttpMethods.HEAD)
                && body != null
                && !body.isEmpty();
        List<Header> defaultHeaders;
        try {
            defaultHeaders = core.network().generateHeaders(
                    new String[]{},
                    new ArrayList<Pair<String, String>>(),
                    false,
                    hasBody
            );
        } catch (Exception e) {
            return null;
        }
        if (headers != null)
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getValue() != null) {
                    defaultHeaders.add(new CustomHeader(header.getKey(), header.getValue()));
                }
            }

        return requestBuilder
                .isFormEncoded(false)
                .method(method)
                .headers(defaultHeaders)
                .isFormEncoded(false)
                .url(baseUrl + "v2/" + path)
                .vars(getParams != null ? getParams : new HashMap<String, String>())
                .body(body)
                .build();
    }


    void sendRequest(
            final String method,
            final String path,
            final String baseUrl,
            final Map<String, String> headers,
            final Map<String, String> getParams,
            final String body,
            final String requestId,
            final String cb,
            final String profilingKey,
            final JsApiResponseCallback callback
    ) {
        final String sarHash;
        if (profilingKey != null && !profilingKey.isEmpty()) {
            sarHash = core.statistic().profiling().addTask(profilingKey);
        } else {
            sarHash = null;
        }
        if (baseUrl == null) {
            invokeCallback(requestId, 12163, null, null, cb, callback);
            return;
        }
        Request request = generateRequest(method, path, baseUrl, headers, getParams, body);
        if (request == null) {
            invokeCallback(requestId, 12163, null, null, cb, callback);
            return;
        }
        core.network().enqueue(
                request,
                new NoTypeNetworkCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        core.statistic().profiling().setReady(sarHash);
                        invokeCallbackFromResponse(response, requestId, cb, callback);

                    }

                    @Override
                    public void onFailure(Response response) {
                        core.statistic().profiling().setReady(sarHash);
                        invokeCallbackFromResponse(response, requestId, cb, callback);
                    }
                }
        );
    }

    private void invokeCallbackFromResponse(
            Response response,
            String requestId,
            String cb,
            JsApiResponseCallback callback
    ) {
        JSONObject jheaders = new JSONObject();
        if (response.headers != null && response.headers.size() > 0) {
            try {
                for (Map.Entry<String, String> header : response.headers.entrySet()) {
                    if (header.getValue() != null) {
                        jheaders.put(header.getKey(), header.getValue());
                    }
                }
            } catch (JSONException e) {

            }
        }
        invokeCallback(
                requestId,
                response.code,
                response.body != null ? response.body : response.errorBody,
                jheaders.toString(),
                cb,
                callback
        );
    }

    private void invokeCallback(
            String requestId,
            int code,
            String data,
            String headers,
            String cb,
            JsApiResponseCallback callback
    ) {
        try {
            JSONObject resultJson = new JSONObject();
            resultJson.put("requestId", requestId);
            resultJson.put("status", code);
            if (data != null)
                resultJson.put("data", escapeString(data));
            try {
                if (headers != null)
                    resultJson.put("headers", new JSONObject(headers));
            } catch (Exception e) {
            }
            callback.onJsApiResponse(resultJson.toString(), cb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
