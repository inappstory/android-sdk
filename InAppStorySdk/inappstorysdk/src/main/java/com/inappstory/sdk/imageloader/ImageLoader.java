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
import com.inappstory.sdk.lrudiskcache.CacheType;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
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
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    ExecutorService widgetImageExecutorService;

    static ImageLoader loader = null;

    public static ImageLoader getInstance() {
        return loader;
    }

    Context mContext;

    public ImageLoader(Context context) {
        mContext = context;
        executorService = Executors.newFixedThreadPool(1);
        widgetImageExecutorService = Executors.newFixedThreadPool(1);
        loader = this;
    }

    int stub_id = R.drawable.ic_stories_close;

    public void displayImage(String path, int loader, ImageView imageView) {
        displayImage(path, loader, imageView, null);
    }

    public void displayImage(String path, int loader, ImageView imageView, LruDiskCache cache) {
        try {
            stub_id = loader;
            imageViews.put(imageView, path);
            Bitmap bitmap = memoryCache.get(path);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                queuePhoto(path, imageView, cache);
            }
        } catch (Exception e) {

        }
    }


    private void queuePhoto(String url, ImageView imageView, LruDiskCache cache) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, cache);
        executorService.submit(new PhotosLoader(p));
    }

    public Bitmap getBitmap(String url, LruDiskCache cache) {
        if (url == null) return null;

        Bitmap bitmap = null;
        try {
            DownloadFileState fileState = Downloader.downloadOrGetFile(url, false, cache, null, null);
            if (fileState == null || fileState.file == null) return null;
            bitmap = decodeFile(fileState.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = Sizes.dpToPxExt(800, mContext);
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
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
    }


}
