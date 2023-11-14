package com.inappstory.sdk.core.utils.network.utils;

import org.brotli.dec.BrotliInputStream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class ResponseStringFromStream {
    public String get(InputStream inputStream, String decompression) throws Exception {
        BufferedReader bufferedReader;
        if (decompression != null) {
            switch (decompression) {
                case "br":
                    bufferedReader = new BufferedReader(new InputStreamReader(new BrotliInputStream(inputStream)));
                    break;
                case "gzip":
                    bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream)));
                    break;
                default:
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            }
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        }

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return response.toString();
    }
}
