package com.inappstory.sdk.core.utils.network.utils;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.utils.network.models.Response;
import com.inappstory.sdk.core.utils.network.models.ResponseWithRawData;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class RawResponseFromConnection {
    ResponseWithRawData get(HttpURLConnection connection, String requestId) throws Exception {
        ResponseWithRawData responseWithRawData = new ResponseWithRawData();
        int responseCode = connection.getResponseCode();
        InAppStoryManager.showDLog("InAppStory_Network", requestId + " " + connection.getURL().toString() + " \nStatus Code: " + responseCode);
        long contentLength = 0;
        String decompression = null;
        HashMap<String, String> responseHeaders = new ConnectionHeadersMap().get(connection);
        if (responseHeaders.containsKey("Content-Encoding")) {
            decompression = responseHeaders.get("Content-Encoding");
        }
        if (responseHeaders.containsKey("content-encoding")) {
            decompression = responseHeaders.get("content-encoding");
        }
        ResponseStringFromStream stringFromStream = new ResponseStringFromStream();
        responseWithRawData.responseCode = responseCode;
        if (responseCode < 400) {
            responseWithRawData.decompressedStream = stringFromStream.get(connection.getInputStream(), decompression);
            InAppStoryManager.showDLog("InAppStory_Network",
                    requestId + " Response: " + responseWithRawData.decompressedStream);

            responseWithRawData.response = new Response.Builder().contentLength(contentLength).
                    headers(responseHeaders).code(responseCode).body(responseWithRawData.decompressedStream).build();
        } else {
            responseWithRawData.decompressedStream = stringFromStream.get(connection.getErrorStream(), decompression);
            InAppStoryManager.showDLog("InAppStory_Network",
                    requestId + " Error: " + responseWithRawData.decompressedStream);
            responseWithRawData.response = new Response.Builder().contentLength(contentLength).
                    headers(responseHeaders).code(responseCode).errorBody(responseWithRawData.decompressedStream).build();
        }
        return responseWithRawData;
    }
}
