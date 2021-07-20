package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.stories.api.models.StatisticSession;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.inappstory.sdk.network.NetworkClient.getUAString;
import static com.inappstory.sdk.network.NetworkHandler.GET;
import static com.inappstory.sdk.network.NetworkHandler.getResponseFromStream;

public class GameNetwork {

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

    public static GameResponse sendRequest(String method,
                                           String path,
                                           Map<String, String> headers,
                                           Map<String, String> getParams,
                                           String body,
                                           String requestId, Context context) throws Exception {
        GameResponse response = new GameResponse();
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
        connection.setRequestProperty("auth-session-id", StatisticSession.getInstance().id);
        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }
        }
        if (!method.equals(GET) && body != null && !body.isEmpty()) {
            connection.setDoOutput(true);
            OutputStream outStream = connection.getOutputStream();
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
            outStreamWriter.write(body);
            outStreamWriter.flush();
            outStreamWriter.close();
            outStream.close();
        }
        int statusCode = connection.getResponseCode();
        response.status = statusCode;
        if (connection.getHeaderFields() != null && connection.getHeaderFields().size() > 0) {
            JSONObject jheaders = new JSONObject();
            for (String headerKey : connection.getHeaderFields().keySet()) {
                if (connection.getHeaderFields().get(headerKey) != null &&
                        connection.getHeaderFields().get(headerKey).size() > 0) {
                    if (headerKey != null)
                        jheaders.put(headerKey, connection.getHeaderFields().get(headerKey).get(0));
                }
            }
            response.headers = jheaders.toString();
        }
        String respBody = null;
        try {
            respBody = getResponseFromStream(connection.getInputStream());
        } catch (IOException e) {
            respBody = getResponseFromStream(connection.getErrorStream());
        }
        response.data = respBody;
        connection.disconnect();
        return response;
    }
}
