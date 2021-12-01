package com.inappstory.sdk.stories.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.CacheFontObject;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;


/**
 * Загрузчик файлов
 */

public class Downloader {

    /**
     * Получение ссылки без параметров
     *
     * @param url ссылка
     */
    public static String cropUrl(String url) {
        return url;//url.split("\\?")[0];
    }

    public static void downloadFonts(List<CacheFontObject> cachedFonts) {
        if (cachedFonts != null) {
            for (CacheFontObject cacheFontObject : cachedFonts) {
                downFontFile(cacheFontObject.url, InAppStoryService.getInstance().getCommonCache());
            }
        }
    }


    @NonNull
    @WorkerThread
    public static File downloadOrGetFile(@NonNull String url,
                                         LruDiskCache cache, File img, FileLoadProgressCallback callback) throws Exception {
        return downloadOrGetFile(url, cache, img, callback, null);
    }

    @NonNull
    @WorkerThread
    public static File downloadOrGetFile(@NonNull String url,
                                         LruDiskCache cache, File img, FileLoadProgressCallback callback, String hash) throws Exception {
        String key = cropUrl(url);
        if (cache.hasKey(key)) {
            return cache.get(key);
        } else {
            if (hash != null) {
                ProfilingManager.getInstance().addTask("game_download", hash);
            }
            if (img == null) {
                img = cache.getFileFromKey(key);
            }
            File file = downloadFile(url, img, callback);
            cache.put(key, file);
            return file;
        }
    }

    @NonNull
    @WorkerThread
    public static boolean downloadOrGetGameFile(@NonNull String url, @NonNull String hashKey,
                                             LruDiskCache cache, File img, FileLoadProgressCallback callback) throws Exception {
        String key = hashKey + "_" + cropUrl(url);
        if (cache.hasKey(key)) {
            return false;
        } else {
            if (img == null) {
                img = cache.getFileFromKey(key);
            }
            File file = downloadFile(url, img, callback);
            cache.put(key, file);
            return true;
        }
    }


    public static File getCoverVideo(@NonNull String url,
                                     LruDiskCache cache) throws IOException {
        String key = cropUrl(url);
        if (cache.hasKey(key)) {
            return cache.get(key);
        } else {
            return null;
        }
    }


    private static final ExecutorService fontDownloader = Executors.newFixedThreadPool(1);
    private static final ExecutorService tmpFileDownloader = Executors.newFixedThreadPool(1);

    private static void downFontFile(final String url, final LruDiskCache cache) {
        fontDownloader.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return downloadOrGetFile(url, cache, null, null);
            }
        });
    }

    public static void downloadCoverVideo(final String url, final LruDiskCache cache) {
        tmpFileDownloader.submit(new Callable() {
            @Override
            public File call() throws Exception {
                return downloadOrGetFile(url, cache, null, null);
            }
        });
    }


    public static String getFontFile(String url) {
        if (url == null || url.isEmpty()) return null;
        File img = null;
        if (InAppStoryService.getInstance().getCommonCache().hasKey(url)) {
            try {
                img = InAppStoryService.getInstance().getCommonCache().get(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (img != null && img.exists()) {
            return img.getAbsolutePath();
        }
        return null;
    }

    private static File downloadFile(String url, File outputFile, FileLoadProgressCallback callback) throws Exception {

        outputFile.getParentFile().mkdirs();
        if (!outputFile.exists())
            outputFile.createNewFile();


        URL urlS = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlS.openConnection();
        urlConnection.setConnectTimeout(300000);
        urlConnection.setReadTimeout(300000);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();


        FileOutputStream fileOutput = new FileOutputStream(outputFile);
        FileLock lock = fileOutput.getChannel().lock();
        InputStream inputStream = urlConnection.getInputStream();

        String contentType = urlConnection.getHeaderField("Content-Type");
        if (urlConnection.getResponseCode() > 350) {
            lock.release();
            throw new RuntimeException();
        }
        if (contentType != null)
            KeyValueStorage.saveString(outputFile.getName(), contentType);
        else
            KeyValueStorage.saveString(outputFile.getName(), "image/jpeg");

        int sz = urlConnection.getContentLength();

        byte[] buffer = new byte[1024];
        int bufferLength = 0;
        int cnt = 0;
        while ((bufferLength = inputStream.read(buffer)) > 0) {
            fileOutput.write(buffer, 0, bufferLength);
            cnt += bufferLength;
            if (callback != null)
                callback.onProgress(cnt, sz);
        }
        fileOutput.flush();
        try {
            lock.release();
        } catch (Exception e) {
        }
        fileOutput.close();
        return outputFile;

    }

    public static void compressFile(File srcFile, String mimeType) throws IOException {
        FileOutputStream out = new FileOutputStream(srcFile.getAbsolutePath());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 2;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(srcFile.getPath(), options);
            switch (mimeType) {
                case "image/jpeg":
                case "image/jpg":
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                    break;
                case "image/png":
                    bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
                    break;
                case "image/webp":
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 70, out);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.close();
    }
}
