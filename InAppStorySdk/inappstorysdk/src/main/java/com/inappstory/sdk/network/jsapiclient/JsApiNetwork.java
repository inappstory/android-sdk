package com.inappstory.sdk.network.jsapiclient;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.constants.HttpMethods;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.network.utils.headers.CustomHeader;
import com.inappstory.sdk.network.utils.headers.Header;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsApiNetwork {


    public static JsApiResponse sendRequest(
            final String method,
            final String path,
            final String baseUrl,
            final Map<String, String> headers,
            final Map<String, String> getParams,
            final String body,
            String requestId,
            final Context context
    ) throws Exception {
        final JsApiResponse jsResponse = new JsApiResponse();
        jsResponse.requestId = requestId;
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {

                if (core.network().getBaseUrl() == null) {
                    jsResponse.status = 12163;
                } else {
                    new ConnectionCheck().check(context, new ConnectionCheckCallback(core) {
                        @Override
                        public void success() {
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
                                jsResponse.status = 12163;
                                return;
                            }
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
                            Response networkResponse = core.network().execute(request);
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
                        }

                        @Override
                        protected void error() {
                            super.error();
                            jsResponse.status = 12163;
                        }
                    });
                }
            }

            @Override
            public void error() {
                jsResponse.status = 12163;
            }
        });
        return jsResponse;
    }
}
