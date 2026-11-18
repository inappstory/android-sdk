package com.inappstory.sdk.network.utils;

import android.util.Pair;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.network.models.ResponseWithRawData;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawResponseFromConnection {
    ResponseWithRawData get(HttpURLConnection connection, String requestId) throws Exception {
        ResponseWithRawData responseWithRawData = new ResponseWithRawData();
        int responseCode = connection.getResponseCode();
        InAppStoryManager.showDLog(LoggerTags.IAS_NETWORK, requestId + " " + connection.getURL().toString() + " \nStatus Code: " + responseCode);
        long contentLength = 0;
        String decompression = null;
        Map<String, String> responseHeaders = new ConnectionHeadersMap().get(connection);
        if (responseHeaders.containsKey("Content-Encoding")) {
            decompression = responseHeaders.get("Content-Encoding");
        }
        if (responseHeaders.containsKey("content-encoding")) {
            decompression = responseHeaders.get("content-encoding");
        }
        ResponseStringFromStream stringFromStream = new ResponseStringFromStream();
        responseWithRawData.responseCode = responseCode;
        StringBuilder headers = new StringBuilder();
        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
            headers.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        if (headers.length() > 0) {
            headers = new StringBuilder(headers.substring(0, headers.length() - 1));
            InAppStoryManager.showDLog(LoggerTags.IAS_NETWORK,
                    requestId + " Headers:\n" + headers);
        }
        if (responseCode < 400) {
            responseWithRawData.decompressedStream = stringFromStream.get(connection.getInputStream(), decompression);
            InAppStoryManager.showDLog(LoggerTags.IAS_NETWORK,
                    requestId + " Response: " + responseWithRawData.decompressedStream);

            responseWithRawData.response = new Response.Builder().contentLength(contentLength).
                    headers(responseHeaders).code(responseCode).body(responseWithRawData.decompressedStream).build();
        } else {
            responseWithRawData.decompressedStream = stringFromStream.get(connection.getErrorStream(), decompression);
            InAppStoryManager.showDLog(LoggerTags.IAS_NETWORK,
                    requestId + " Error: " + responseWithRawData.decompressedStream);
            responseWithRawData.response = new Response.Builder().contentLength(contentLength).
                    headers(responseHeaders).code(responseCode).errorBody(responseWithRawData.decompressedStream).build();
        }
        return responseWithRawData;
    }
}
