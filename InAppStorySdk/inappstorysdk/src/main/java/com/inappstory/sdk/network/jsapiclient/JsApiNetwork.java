package com.inappstory.sdk.network.jsapiclient;

import static com.inappstory.sdk.network.NetworkClient.getUAString;
import static com.inappstory.sdk.network.NetworkHandler.GET;
import static com.inappstory.sdk.network.NetworkHandler.getResponseFromStream;
import static java.util.UUID.randomUUID;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequestHeader;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class JsApiNetwork {

    static URL getURL(String path, Map<String, String> queryParams) throws Exception {
        String url = NetworkClient.getInstance().getBaseUrl() + "v2/" + path;
        String varStr = "";
        if (queryParams != null && queryParams.keySet().size() > 0) {
            for (Object key : queryParams.keySet()) {
                varStr += "&" + key + "=" + queryParams.get(key);
            }
            varStr = "?" + varStr.substring(1);
        }
        return new URL(url + varStr);
    }

    public static JsApiResponse sendRequest(String method,
                                            String path,
                                            Map<String, String> headers,
                                            Map<String, String> getParams,
                                            String body,
                                            String requestId, Context context) throws Exception {


        JsApiResponse response = new JsApiResponse();
        response.requestId = requestId;
        if (!InAppStoryService.isConnected()) {
            response.status = 12163;
            return response;
        }
        HttpURLConnection connection = (HttpURLConnection) getURL(path, getParams).openConnection();

        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod(method);
        String packageName = context.getPackageName();
        String language;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            language = Locale.getDefault().toLanguageTag();
        } else {
            language = Locale.getDefault().getLanguage();
        }
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Accept-Language", language);
        connection.setRequestProperty("X-APP-PACKAGE-ID", packageName != null ? packageName : "-");
        connection.setRequestProperty("User-Agent", getUAString(context));
        connection.setRequestProperty("Authorization", "Bearer " + ApiSettings.getInstance().getApiKey());
        connection.setRequestProperty("X-Device-Id", Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        connection.setRequestProperty("X-Request-ID", randomUUID().toString());
        if (InAppStoryService.isNotNull())
            connection.setRequestProperty("X-User-id", InAppStoryService.getInstance().getUserId());
        connection.setRequestProperty("auth-session-id", Session.getInstance().id);

        boolean hasBody = !method.equals(GET) && body != null && !body.isEmpty();
        if (hasBody) {
            connection.setRequestProperty("Content-Type", "application/json");
        }
        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }
        }
        ApiLogRequest requestLog = new ApiLogRequest();
        String logRequestId = UUID.randomUUID().toString();
        requestLog.id = logRequestId;
        requestLog.url = connection.getURL().toString();
        requestLog.timestamp = System.currentTimeMillis();
        requestLog.method = method;
        for (Map.Entry<String, List<String>> entry : connection.getRequestProperties().entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty())
                requestLog.headers.add(new ApiLogRequestHeader(entry.getKey(), entry.getValue().get(0)));
        }
        if (hasBody) {
            requestLog.body = body;
            InAppStoryManager.sendApiRequestLog(requestLog);
            connection.setDoOutput(true);
            OutputStream outStream = connection.getOutputStream();
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
            outStreamWriter.write(body);
            outStreamWriter.flush();
            outStreamWriter.close();
            outStream.close();
        } else {
            InAppStoryManager.sendApiRequestLog(requestLog);
        }
        int statusCode = connection.getResponseCode();
        response.status = statusCode;
        HashMap<String, String> logHeaders = new HashMap<>();
        if (connection.getHeaderFields() != null && connection.getHeaderFields().size() > 0) {
            JSONObject jheaders = new JSONObject();
            for (String headerKey : connection.getHeaderFields().keySet()) {
                if (connection.getHeaderFields().get(headerKey) != null &&
                        connection.getHeaderFields().get(headerKey).size() > 0) {
                    if (headerKey != null) {
                        String headerVal = connection.getHeaderFields().get(headerKey).get(0);
                        jheaders.put(headerKey, headerVal);
                        logHeaders.put(headerKey, headerVal);
                    }
                }
            }
            response.headers = jheaders.toString();
        }
        String respBody = null;
        ApiLogResponse responseLog = new ApiLogResponse();
        responseLog.id = logRequestId;
        responseLog.timestamp = System.currentTimeMillis();
        responseLog.contentLength = connection.getContentLength();
        try {
            respBody = getResponseFromStream(connection.getInputStream());
            responseLog.generateJsonResponse(response.status, respBody, logHeaders);
        } catch (IOException e) {
            InAppStoryService.createExceptionLog(e);
            respBody = getResponseFromStream(connection.getErrorStream());
            responseLog.generateError(response.status, respBody, logHeaders);
        }
        response.data = respBody;
        connection.disconnect();
        InAppStoryManager.sendApiResponseLog(responseLog);
        return response;
    }
}
