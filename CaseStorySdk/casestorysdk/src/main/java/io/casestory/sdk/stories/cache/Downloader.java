package io.casestory.sdk.stories.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.casestory.sdk.stories.api.networkclient.ApiClient;
import io.casestory.sdk.stories.utils.KeyValueStorage;
import okhttp3.Request;
import okhttp3.Response;


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

    /**
     * Скачиваем и при необходимости сжимаем файл
     *
     * @param con      контекст приложения
     * @param url      внешняя ссылка на сервер, откуда качаем файл
     * @param type     тип контента
     * @param sourceId идентификатор контента для генерации пути
     * @param size     максимальный необходимый размер изображений в прикселях
     */
    @NonNull
    @WorkerThread
    public static File downAndCompressImg(Context con,
                                          @NonNull String url,
                                          String type,
                                          Integer sourceId,
                                          Point size) throws IOException {
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
        byte[] bytes = null;
        Response response = ApiClient.getImageApiOk().newCall(
                new Request.Builder().url(url).build()).execute();
        String ctType = response.header("Content-Type");
        boolean isPng = false;
        if (ctType != null && ctType.equals("image/png")) isPng = true;
        bytes = response.body().bytes();
        File file = downloadFile(bytes, img, ctType);
        return file;
    }


    @NonNull
    @WorkerThread
    public static File downVideo(Context con,
                                 @NonNull String url,
                                 String type,
                                 Integer sourceId,
                                 Point size) throws IOException {
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
        byte[] bytes = null;
        Response response = ApiClient.getImageApiOk().newCall(
                new Request.Builder().url(url).build()).execute();
        String ctType = response.header("Content-Type");
        bytes = response.body().bytes();
        File file = downloadFile(bytes, img, ctType);
        return file;
    }

    @NonNull
    @WorkerThread
    public static byte[] downAndCompressBytes(Context con,
                                              @NonNull String url,
                                              String type,
                                              Integer sourceId,
                                              Point size) throws IOException {
        FileCache cache = FileCache.INSTANCE;

        File img = cache.getStoredFile(con, cropUrl(url), type, sourceId, null);
        File img2;
        if (img.exists()) {
            return readFile(img);
        } else {
            img2 = cache.getStoredFile(con, cropUrl(url), FileType.TEMP_FILE, null, null);
            if (img2.exists()) {
                cache.moveFileToStorage(con, cropUrl(url), type, sourceId, null);
                return readFile(img2);
            }
        }
        byte[] bytes = null;
        //  Log.e("urlCrash", url);
        bytes = ApiClient.getImageApiOk().newCall(
                new Request.Builder().url(url).build()).execute().body().bytes();
        File file = saveCompressedImg(bytes, img, size, false);
        return readFile(file);
    }

    public static byte[] readFile(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return bytes;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Скачиваем и при необходимости сжимаем файл
     *
     * @param con  контекст приложения
     * @param url  внешняя ссылка на сервер, откуда качаем файл
     * @param type тип контента
     * @param id   идентификатор контента для генерации пути
     * @return возвращаем файл по нужному пути (новый или ранее записанный)
     */

    private static final ExecutorService imageExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    public static void downFontFile(final Context con, final String url) {
        final Future<File> ff = imageExecutor.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return downFile(con, url, FileType.TEMP_FILE, null);
            }
        });
        runnableExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ff.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
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

    @NonNull
    @WorkerThread
    public static File downFile(Context con, String url, String type, Integer id) throws IOException {
        if (url == null || url.isEmpty()) return null;
        FileCache cache = FileCache.INSTANCE;
        File img = cache.getStoredFile(con, cropUrl(url), type, id, null);
        File img2;
        if (img.exists()) {
            return img;
        } else {
            img2 = cache.getStoredFile(con, cropUrl(url), FileType.TEMP_FILE, null, null);
            if (img2.exists()) {
                if (type != FileType.TEMP_FILE) {
                    cache.moveFileToStorage(con, cropUrl(url), type, id, null);
                }

                return img2;
            }
        }
        byte[] bytes = null;
        okhttp3.Response response;
        response = ApiClient.getImageApiOk().newCall(
                new Request.Builder().url(url).build()).execute();
        bytes = response.body().bytes();
        try {
            img.getParentFile().mkdirs();
            img.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(img);
            fout.write(bytes);
            fout.close();
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Получение битмапа из байтов
     *
     * @param res байты изображения
     */
    private static Bitmap decodeSampledBitmapFromResource(byte[] res) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, 0, res.length, options);
        double coeff = Math.sqrt(res.length / 500000);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, (int) (options.outWidth / coeff), (int) (options.outHeight / coeff));

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(res, 0, res.length, options);
    }

    /**
     * Получение битмапа из байтов
     *
     * @param res  байты изображения
     * @param size максимальный размер битмапа в байтах
     */
    private static Bitmap decodeSampledBitmapFromResource(byte[] res, int size) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, 0, res.length, options);
        double coeff = Math.sqrt(res.length / size);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, (int) (options.outWidth / coeff), (int) (options.outHeight / coeff));

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(res, 0, res.length, options);
    }

    /**
     * вычисляем коэффициент необходимого уменьшения
     *
     * @param options   текущие свойства битмапа
     * @param reqWidth  требуемая ширина
     * @param reqHeight требуемая высота
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Сохранение изображения (и сжатие при необходимости)
     *
     * @param bytes       изображение в виде массива байтов
     * @param img         путь к файлу для сохранения
     * @param limitedSize ограничение по размеру
     */
    public static File saveCompressedImg(byte[] bytes, File img, Point limitedSize, boolean isPng) throws IOException {
        if (bytes == null) return null;
        img.getParentFile().mkdirs();
        if (!img.exists())
            img.createNewFile();
        FileOutputStream fos = new FileOutputStream(img);
        Bitmap bm;
        boolean png = false;
        if (bytes.length > 3 && bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            fos.close();
            throw new IOException("It's a GIF file");
        }
        if (bytes.length > 500000)
            try {

                bm = decodeSampledBitmapFromResource(bytes);
            } catch (Exception e) {
                bm = decodeSampledBitmapFromResource(bytes, 150000);
            }
        else
            try {
                bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (Exception e) {
                bm = decodeSampledBitmapFromResource(bytes, 150000);
            }
        if (bm == null) {
            throw new IOException("wrong bitmap");
        }
        if (!isPng) {
            if (limitedSize != null && (bm.getWidth() > limitedSize.x || bm.getHeight() > limitedSize.y)) {
                Bitmap outBm = Bitmap.createScaledBitmap(bm, limitedSize.x, bm.getHeight() * limitedSize.x / bm.getWidth(), true);
                outBm.compress(Bitmap.CompressFormat.JPEG, 90,
                        new BufferedOutputStream(fos, 1024));
                bm.recycle();
                outBm.recycle();
            } else {
                bm.compress(Bitmap.CompressFormat.JPEG, 90,
                        new BufferedOutputStream(fos, 1024));
                bm.recycle();
            }
        } else {
            bm.compress(Bitmap.CompressFormat.PNG, 90,
                    new BufferedOutputStream(fos, 1024));
            bm.recycle();
        }
        fos.close();
        return img;
    }


    public static File downloadFile(byte[] bytes, File outputFile, String contentType) throws IOException {
        if (bytes == null) return null;
        outputFile.getParentFile().mkdirs();
        if (!outputFile.exists())
            outputFile.createNewFile();
        if (contentType != null)
            KeyValueStorage.saveString(outputFile.getName(), contentType);
        else
            KeyValueStorage.saveString(outputFile.getName(), "image/jpeg");
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(bytes);
        fos.flush();
        fos.close();
        return outputFile;

    }
}
