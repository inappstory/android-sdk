package com.inappstory.sdk.imageloader;

import static com.inappstory.sdk.InAppStoryService.IAS_PREFIX;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.generated.GeneratedImageView;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    MemoryCache memoryCache2 = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    ExecutorService widgetImageExecutorService;

    static ImageLoader loader = null;

    public static ImageLoader getInstance() {
        return loader;
    }

    Context mContext;

    public ImageLoader(Context context) {
        memoryCache2 = new MemoryCache();
        mContext = context;
        executorService = Executors.newFixedThreadPool(1);
        widgetImageExecutorService = Executors.newFixedThreadPool(1);
        loader = this;
    }

    int stub_id = R.drawable.ic_stories_close;

    public void displayImage(String path, int loader, ImageView imageView) {
        displayImage(path, loader, imageView, null);
    }

    public HashMap<String, String> fileLinks = new HashMap<>();
    public Object fileLinksLock = new Object();

    public String getFileLink(String link) {
        synchronized (fileLinksLock) {
            return fileLinks.get(link);
        }
    }

    public void addLink(String link, String fileLink) {
        synchronized (fileLinksLock) {
            fileLinks.put(link, fileLink);
        }
    }

    public void clearFileLinks() {
        synchronized (fileLinksLock) {
            fileLinks.clear();
        }
    }

    public void displayImage(String path, int loader, ImageView imageView, LruDiskCache cache) {
        try {
            stub_id = loader;
            imageViews.put(imageView, path);
            Bitmap bitmap = memoryCache.get(path);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                if (imageView instanceof GeneratedImageView) {
                    ((GeneratedImageView) imageView).onLoaded();
                }
            } else {
                queuePhoto(path, imageView, cache);
            }
        } catch (Exception e) {

        }
    }

    LruDiskCache cache;

    public void displayRemoteImage(final String url, int loader, final RemoteViews rv, final int id, final Integer cornerRadius, final Float ratio, Context context) {
        try {
            stub_id = loader;
            if (memoryCache2 == null) memoryCache2 = new MemoryCache();
            final Bitmap[] bitmap = {memoryCache2.get(url)};
            if (bitmap[0] != null)
                rv.setImageViewBitmap(id, bitmap[0]);
            else {
                if (cache == null) {
                    cache = LruDiskCache.create(

                            context.getCacheDir(),
                            IAS_PREFIX + "fastCache",
                            MB_10, true);
                }
                bitmap[0] = getWidgetBitmap(url, cornerRadius, true, ratio, null, cache);
                memoryCache2.put(url, bitmap[0]);
                rv.setImageViewBitmap(id, bitmap[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayRemoteColor(String color, int loader, RemoteViews rv, int id, Integer cornerRadius, Float ratio, Context context) {
        try {
            stub_id = loader;
            Bitmap bitmap = memoryCache2.get(color);
            if (bitmap != null)
                rv.setImageViewBitmap(id, bitmap);
            else {
                if (cache == null) {
                    cache = LruDiskCache.create(
                            context.getCacheDir(),
                            IAS_PREFIX + "fastCache",
                            MB_10, true);
                }
                bitmap = getWidgetBitmap(null, cornerRadius, true, ratio, color, cache);
                memoryCache2.put(color, bitmap);
                rv.setImageViewBitmap(id, bitmap);
            }
        } catch (Exception e) {

        }
    }

    private void queuePhoto(String url, ImageView imageView, LruDiskCache cache) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, cache);
        executorService.submit(new PhotosLoader(p));
    }

    public void addDarkGradient(Bitmap bitmap) {
        if (bitmap == null) return;
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(createShader(bitmap.getWidth(), bitmap.getHeight()));
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
    }

    private Shader createShader(int x1, int y1) {
        LinearGradient shader = new LinearGradient(0, 0, 0, y1,
                new int[]{Color.TRANSPARENT, Color.parseColor("#AA000000")}, null,
                Shader.TileMode.REPEAT);
        return shader;
    }

    public Bitmap getBitmap(String url, LruDiskCache cache) {
        if (url == null) return null;

        Bitmap bitmap = null;
        try {
            File file = Downloader.downloadOrGetFile(url, cache, null, null);
            if (file == null) return null;
            bitmap = decodeFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getWidgetBitmap(String url, Integer pixels, boolean getThumbnail, Float ratio, String color, LruDiskCache lruDiskCache) {
        if (url == null && color == null) return null;
        String spixels;
        String sratio;
        if (pixels == null) {
            spixels = memoryCache2.getSettings("pixels");
            if (spixels != null) {
                pixels = Integer.parseInt(spixels);
            }
        } else {
            memoryCache2.putSettings("pixels", Integer.toString(pixels));
        }
        if (ratio == null) {
            sratio = memoryCache2.getSettings("ratio");
            if (sratio != null) {
                ratio = Float.parseFloat(sratio);
            }
        } else {
            memoryCache2.putSettings("ratio", Float.toString(ratio));
        }

        if (url == null) {
            Bitmap bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(Color.parseColor(color));
            if (getThumbnail) {
                if (ratio != null && ratio > 0) {
                    bmp = ThumbnailUtils.extractThumbnail(bmp, (int) (ratio * 300), 300);
                } else {
                    bmp = ThumbnailUtils.extractThumbnail(bmp, 300, 300);
                }
            }
            addDarkGradient(bmp);
            if (pixels != null)
                bmp = getRoundedCornerBitmap(bmp, pixels);
            return bmp;
        }
        try {
            File f = Downloader.downloadOrGetFile(url, lruDiskCache, null, null);
            if (f == null) return null;
            Bitmap bitmap = decodeFile(f);
            if (getThumbnail) {
                if (ratio != null && ratio > 0) {
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int) (ratio * 300), 300);
                } else {
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                }
            }
            addDarkGradient(bitmap);
            if (pixels != null)
                bitmap = getRoundedCornerBitmap(bitmap, pixels);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = Sizes.dpToPxExt(800);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream fileInputStream = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream, null, o2);
            fileInputStream.close();
            return bitmap;
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
        return null;
    }

    private Bitmap decodeStream(InputStream stream) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, o);

        final int REQUIRED_SIZE = Sizes.dpToPxExt(800);
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(stream, null, o2);
    }

    private class PhotoToLoad {
        public String path;
        public ImageView imageView;
        public LruDiskCache cache;

        public PhotoToLoad(String u, ImageView i, LruDiskCache c) {
            path = u;
            imageView = i;
            cache = c;
        }
    }


    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = null;
            if (photoToLoad.cache != null)
                bmp = getBitmap(photoToLoad.path, photoToLoad.cache);
            else
                bmp = decodeFile(new File(photoToLoad.path));
            if (bmp != null)
                memoryCache.put(photoToLoad.path, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            new Handler(Looper.getMainLooper()).post(bd);
        }
    }

    public boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.path))
            return true;
        return false;
    }

    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
                if (photoToLoad.imageView instanceof GeneratedImageView) {
                    ((GeneratedImageView) photoToLoad.imageView).onLoaded();
                }
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        memoryCache2.clear();
    }

    public void clearWidgetCache() {
        memoryCache2.clear();
    }

}
