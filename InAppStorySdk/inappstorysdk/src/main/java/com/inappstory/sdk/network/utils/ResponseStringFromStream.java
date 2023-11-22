package com.inappstory.sdk.network.utils;

import org.brotli.dec.BrotliInputStream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class ResponseStringFromStream {
    public String get(InputStream inputStream, String decompression) throws Exception {
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                getInputStream(
                                        inputStream, decompression
                                )
                        )
                );
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return response.toString();
    }

    public InputStream getInputStream(InputStream inputStream, String decompression) throws Exception {
        if (decompression != null) {
            switch (decompression) {
                case "br":
                    return new BrotliInputStream(inputStream);
                case "gzip":
                    return new GZIPInputStream(inputStream);
                default:
                    return inputStream;
            }
        } else {
            return inputStream;
        }
    }
}
