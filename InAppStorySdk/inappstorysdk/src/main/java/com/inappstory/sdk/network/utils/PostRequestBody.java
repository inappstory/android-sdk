package com.inappstory.sdk.network.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class PostRequestBody {
    void writeToStream(HttpURLConnection connection, String body) throws IOException {
        connection.setDoOutput(true);
        OutputStream outStream = connection.getOutputStream();
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
        outStreamWriter.write(body);
        outStreamWriter.flush();
        outStreamWriter.close();
        outStream.close();
    }
}
