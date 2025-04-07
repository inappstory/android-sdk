package com.inappstory.sdk.stories.cache.vod;

import android.util.Pair;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.utils.ConnectionHeadersMap;
import com.inappstory.sdk.network.utils.ResponseStringFromStream;
import com.inappstory.sdk.utils.format.StringsUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class VODDownloader {

    private final IASCore core;

    public VODDownloader(IASCore core) {
        this.core = core;
    }

    public boolean putBytesToFile(
            long position,
            byte[] bytes,
            String filePath
    ) {
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            final RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(position);
            fos = new FileOutputStream(raf.getFD()) {
                @Override
                public void close() throws IOException {
                    super.close();
                    raf.close();
                }
            };
            fos.write(bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {

            }
        }
        return false;
    }

    public Pair<ContentRange, byte[]> getBytesFromFile(ContentRange contentRange, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        long bytesAmount = contentRange.end() - contentRange.start();
        if (bytesAmount > (long) Integer.MAX_VALUE) {
            return null;
        }
        byte[] data = new byte[Long.valueOf(bytesAmount).intValue()];
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            raf.seek(contentRange.start());
            raf.read(data);
            return new Pair<>(contentRange, data);
        } catch (IOException e) {

        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {

            }
        }
        return null;
    }

    public Pair<ContentRange, byte[]> downloadBytes(
            String url,
            long from,
            long to,
            long freeSpace
    ) throws Exception {
        URL urlS = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlS.openConnection();
        urlConnection.setConnectTimeout(300000);
        urlConnection.setReadTimeout(300000);
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", core.network().userAgent());
        if (from > 0) {
            if (to > 0) {
                urlConnection.setRequestProperty("Range", "bytes=" + from + "-" + to);
            } else {
                urlConnection.setRequestProperty("Range", "bytes=" + from + "-");
            }
        } else {
            if (to > 0) {
                urlConnection.setRequestProperty("Range", "bytes=" + 0 + "-" + to);
            }
        }
        ContentRange contentRange = new ContentRange(from, to, to);
        //String curl = toCurlRequest(urlConnection, null);
        try {
            urlConnection.connect();
        } catch (Exception e) {
            return null;
        }
        int status = urlConnection.getResponseCode();
        HashMap<String, String> headers = new HashMap<>();

        int sz = urlConnection.getContentLength();
        if (freeSpace > 0 && sz > freeSpace) {
            urlConnection.disconnect();
            return null;
        }

        String decompression = null;
        HashMap<String, String> responseHeaders = new ConnectionHeadersMap().get(urlConnection);
        if (responseHeaders.containsKey("Content-Encoding")) {
            decompression = responseHeaders.get("Content-Encoding");
        }
        if (responseHeaders.containsKey("content-encoding")) {
            decompression = responseHeaders.get("content-encoding");
        }
        String rangeHeader = responseHeaders.get("Content-Range");
        if (rangeHeader != null) {
            contentRange = StringsUtils.getRange(rangeHeader, sz);
        }
        ResponseStringFromStream responseStringFromStream = new ResponseStringFromStream();
        if (status > 350) {
            return null;
        }
        byte[] data = new byte[1024];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        InputStream inputStream = responseStringFromStream.getInputStream(
                urlConnection.getInputStream(),
                decompression
        );
        int nRead;

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new Pair<>(contentRange, buffer.toByteArray());
    }
}
