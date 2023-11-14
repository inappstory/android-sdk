package com.inappstory.sdk.core.utils.network.jsapiclient;

import android.content.Context;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.utils.network.constants.HttpMethods;
import com.inappstory.sdk.core.utils.network.models.Request;
import com.inappstory.sdk.core.utils.network.models.Response;
import com.inappstory.sdk.core.utils.network.utils.headers.CustomHeader;
import com.inappstory.sdk.core.utils.network.utils.headers.Header;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsApiNetwork {


    public static JsApiResponse sendRequest(
            String method,
            String path,
            String baseUrl,
            Map<String, String> headers,
            Map<String, String> getParams,
            String body,
            String requestId,
            Context context
    ) throws Exception {
        final JsApiResponse jsResponse = new JsApiResponse();
        jsResponse.requestId = requestId;

        NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (IASCore.getInstance().notConnected() || networkClient == null) {
            jsResponse.status = 12163;
            return jsResponse;
        }
        Request.Builder requestBuilder = new Request.Builder();

        boolean hasBody = !method.equals(HttpMethods.GET)
                && !method.equals(HttpMethods.HEAD)
                && body != null
                && !body.isEmpty();
        List<Header> defaultHeaders = networkClient.generateHeaders(
                context,
                new String[]{},
                false,
                hasBody
        );
        if (headers != null)
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getValue() != null) {
                    defaultHeaders.add(new CustomHeader(header.getKey(), header.getValue()));
                }
            }

        Request request = requestBuilder
                .isFormEncoded(false)
                .method(method)
                .headers(defaultHeaders)
                .isFormEncoded(false)
                .url(baseUrl + "v2/" + path)
                .vars(getParams != null ? getParams : new HashMap<String, String>())
                .body(body)
                .build();
        Response networkResponse = networkClient.execute(request, null);
        jsResponse.status = networkResponse.code;
        if (networkResponse.headers != null && networkResponse.headers.size() > 0) {
            JSONObject jheaders = new JSONObject();
            try {
                for (Map.Entry<String, String> header : networkResponse.headers.entrySet()) {
                    if (header.getValue() != null) {
                        jheaders.put(header.getKey(), header.getValue());
                    }
                }
                jsResponse.headers = jheaders.toString();
            } catch (JSONException e) {

            }
        }
        jsResponse.data = networkResponse.body != null ?
                networkResponse.body :
                networkResponse.errorBody;
        return jsResponse;
    }
}
