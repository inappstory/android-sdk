package com.inappstory.sdk.network.utils;


import android.util.Pair;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.network.constants.HttpMethods;
import com.inappstory.sdk.network.fileupload.FilePart;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.utils.headers.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RequestConnection {
    public Pair<HttpURLConnection, Map<String, List<String>>> build(Request request, String requestId) throws IOException {
        URL url = new GetUrl().fromRequest(request);
        Map<String, List<String>> connectionProperties = null;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod(request.getMethod());
        if (request.getHeaders() != null) {
            for (Object header : request.getHeaders()) {
                connection.setRequestProperty(((Header) header).getKey(), ((Header) header).getValue());
            }
        }
        connectionProperties = connection.getRequestProperties();
        if (request.filePart() != null) {
            String writerLine = "\r\n";
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            String boundary = UUID.randomUUID().toString();
            connection.setRequestProperty(
                    "Content-Type",
                    "multipart/form-data; boundary=" + boundary
            );
            OutputStream outStream = connection.getOutputStream();
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");

            PrintWriter printWriter = new PrintWriter(outStreamWriter, true);
            writeFile(request.filePart(), printWriter, writerLine, outStream, boundary);
            printWriter.append("--" + boundary + "--").append(writerLine);
            printWriter.close();
            outStreamWriter.close();
            outStream.close();
        } else if (!request.getMethod().equals(HttpMethods.GET) &&
                !request.getMethod().equals(HttpMethods.HEAD) &&
                request.getBody() != null &&
                !request.getBody().isEmpty()
        ) {
            InAppStoryManager.showDLog(LoggerTags.IAS_NETWORK, requestId + " " + connectionProperties);
            new PostRequestBody().writeToStream(connection, request.getBody());
            InAppStoryManager.showDLog(LoggerTags.IAS_NETWORK, requestId + " " + request.getBody());
        } else {
            InAppStoryManager.showDLog(LoggerTags.IAS_NETWORK, requestId + " " + connectionProperties);
        }
        return new Pair<>(connection, connectionProperties);
    }

    private void writeFile(
            FilePart filePart,
            PrintWriter writer,
            String divider,
            OutputStream outputStream,
            String boundary
    )
            throws IOException {
        File uploadFile = new File(filePart.filePath());
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(divider);
        writer.append("Content-Disposition: form-data; name=\"" + filePart.fieldName() + "\"; filename=\"" + fileName + "\"")
                .append(divider);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName))
                .append(divider);
        writer.append("Content-Transfer-Encoding: binary").append(divider);
        writer.append(divider);
        writer.flush();
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(divider);
        writer.flush();
    }
}
