package com.inappstory.sdk.newnetwork.utils;

import com.inappstory.sdk.stories.api.models.logs.ApiLogRequestHeader;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class PostRequestBody {
    void writeToStream(HttpURLConnection connection, String body, boolean isFormEncoded) throws IOException {
        if (isFormEncoded) {
            connection.setRequestProperty("Content-Type", "application/json");
        }
        connection.setDoOutput(true);
        OutputStream outStream = connection.getOutputStream();
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
        outStreamWriter.write(body);
        outStreamWriter.flush();
        outStreamWriter.close();
        outStream.close();
    }
}
