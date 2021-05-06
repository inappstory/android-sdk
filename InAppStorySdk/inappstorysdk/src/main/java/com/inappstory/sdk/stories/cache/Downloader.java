package com.inappstory.sdk.stories.cache;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.imageloader.MemoryCache;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.CacheFontObject;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.Sizes;


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
        return url.split("\\?")[0];
    }

    public static void downloadFonts(List<CacheFontObject> cachedFonts) {
        if (cachedFonts != null) {
            for (CacheFontObject cacheFontObject : cachedFonts) {
                downFontFile(InAppStoryManager.getInstance().getContext(), cacheFontObject.url);
            }
        }
    }

    @NonNull
    @WorkerThread
    static File downloadFile(Context con,
                                    @NonNull String url,
                                    String type,
                                    String sourceId) throws Exception {
        FileCache cache = FileCache.INSTANCE;

        File img = cache.getStoredFile(con, cropUrl(url), type, sourceId, null);
        File img2;
        if (img.exists()) {
            return img;
        } else {
            img2 = cache.getStoredFile(con, cropUrl(url), FileType.TEMP_FILE, null, null);
            if (img2.exists()) {
                cache.moveFileToStorage(con, cropUrl(url), type, sourceId, null);
                return img2;
            }
        }
        File file = downloadFile(url, img);
        return file;
    }

    public static File getCoverVideo(Context con,
                                     @NonNull String url,
                                     String type,
                                     String sourceId) {
        FileCache cache = FileCache.INSTANCE;
        File img = cache.getStoredFile(con, cropUrl(url), type, sourceId, null);
        return img;
    }

    private static final ExecutorService fontDownloader = Executors.newFixedThreadPool(1);

    public static void downFontFile(final Context con, final String url) {
        fontDownloader.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return downloadFile(con, url, FileType.TEMP_FILE, null);
            }
        });
    }

    public static String getFontFile(Context con, String url) {
        if (url == null || url.isEmpty()) return null;
        FileCache cache = FileCache.INSTANCE;
        File img = cache.getStoredFile(con, cropUrl(url), FileType.TEMP_FILE, null, null);
        if (img.exists()) {
            return img.getAbsolutePath();
        }
        return null;
    }

    static MemoryCache bitmapCache = new MemoryCache();

    public static void putBitmap(String url, Bitmap bitmap) {
        if (bitmapCache == null) bitmapCache = new MemoryCache();
        bitmapCache.put(url, bitmap);
    }

    public static Bitmap getBitmap(String url) {
        if (bitmapCache == null) bitmapCache = new MemoryCache();
        return bitmapCache.get(url);
    }

    private static File downloadFile(String url, File outputFile) throws Exception {

        outputFile.getParentFile().mkdirs();
        if (!outputFile.exists())
            outputFile.createNewFile();


        URL urlS = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlS.openConnection();
        urlConnection.setConnectTimeout(60000);
        urlConnection.setRequestMethod("GET");
        //urlConnection.setDoOutput(true);
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

        byte[] buffer = new byte[1024];
        int bufferLength = 0;

        while ((bufferLength = inputStream.read(buffer)) > 0) {
            fileOutput.write(buffer, 0, bufferLength);
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
