package com.inappstory.sdk.imageloader;

import android.app.Activity;
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
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.generated.GeneratedImageView;
import com.inappstory.sdk.stories.utils.Sizes;

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    MemoryCache memoryCache2 = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private Map<RemoteViews, String> remoteViews = Collections.synchronizedMap(new HashMap<RemoteViews, String>());
    ExecutorService executorService;
    ExecutorService widgetImageExecutorService;

    static ImageLoader loader = null;

    public static ImageLoader getInstance() {
        return loader;
    }

    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        memoryCache2 = new MemoryCache();
        executorService = Executors.newFixedThreadPool(1);
        widgetImageExecutorService = Executors.newFixedThreadPool(1);
        loader = this;
    }

    int stub_id = R.drawable.ic_stories_close;

    public void displayImage(String url, int loader, ImageView imageView) {
        try {
            stub_id = loader;
            imageViews.put(imageView, url);
            Bitmap bitmap = memoryCache.get(url);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                if (imageView instanceof GeneratedImageView) {
                    ((GeneratedImageView)imageView).onLoaded();
                }
            } else {
                queuePhoto(url, imageView);
                //  imageView.setImageResource(loader);
            }
        } catch (Exception e) {

        }
    }

    public void displayRemoteImage(final String url, int loader, final RemoteViews rv, final int id, final Integer cornerRadius, final Float ratio) {
        try {
            stub_id = loader;
            // remoteViews.put(rv, url);
            if (memoryCache2 == null) memoryCache2 = new MemoryCache();
            final Bitmap[] bitmap = {memoryCache2.get(url)};
            Log.e("MyWidget", url + " " + cornerRadius + " " + ratio);
            if (bitmap[0] != null)
                rv.setImageViewBitmap(id, bitmap[0]);
                //imageView.setImageBitmap(bitmap);
            else {
                bitmap[0] = getWidgetBitmap(url, cornerRadius, true, ratio, null);
                memoryCache2.put(url, bitmap[0]);
                rv.setImageViewBitmap(id, bitmap[0]);
                // queueRemoteImage(url, rv, id, cornerRadius, ratio, null);
                //  imageView.setImageResource(loader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void displayRemoteColor(String color, int loader, RemoteViews rv, int id, Integer cornerRadius, Float ratio) {
        try {
            stub_id = loader;
            // remoteViews.put(rv, color);
            Bitmap bitmap = memoryCache2.get(color);
            if (bitmap != null)
                rv.setImageViewBitmap(id, bitmap);
                //imageView.setImageBitmap(bitmap);
            else {
                bitmap = getWidgetBitmap(null, cornerRadius, true, ratio, color);
                memoryCache2.put(color, bitmap);
                rv.setImageViewBitmap(id, bitmap);
            }
        } catch (Exception e) {

        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private void queueRemoteImage(String url, RemoteViews remoteViews, int id, Integer cornerRadius, Float ratio, String color) {
        RemoteImageToLoad p = new RemoteImageToLoad(url, remoteViews, id, cornerRadius, ratio, color);
        executorService.submit(new RemoteImagesLoader(p));
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

    public Bitmap getBitmap(String url) {
        if (url == null) return null;
        File f = fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f);
        if (b != null) {
            return b;
        }
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            is.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Bitmap getWidgetBitmap(String url, Integer pixels, boolean getThumbnail, Float ratio, String color) {
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
        File f = fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f);
        if (b != null) {
            if (getThumbnail) {
                if (ratio != null && ratio > 0) {
                    b = ThumbnailUtils.extractThumbnail(b, (int) (ratio * 300), 300);
                } else {
                    b = ThumbnailUtils.extractThumbnail(b, 300, 300);
                }
            }
            addDarkGradient(b);
            if (pixels != null)
                b = getRoundedCornerBitmap(b, pixels);
            return b;
        }
        try {
            Bitmap bitmap = null;


            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            is.close();
            bitmap = decodeFile(f);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap decodeStream(InputStream stream) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, o);

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
        return BitmapFactory.decodeStream(stream, null, o2);
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    private class RemoteImageToLoad {
        public String url;
        public RemoteViews imageView;
        public int id;
        public Integer cornerRadius;
        public String color;
        public Float ratio;

        public RemoteImageToLoad(String u, RemoteViews remoteViews, int i, Integer cr, Float r, String c) {
            url = u;
            imageView = remoteViews;
            id = i;
            cornerRadius = cr;
            ratio = r;
            color = c;
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
            Bitmap bmp = getBitmap(photoToLoad.url);
            if (bmp != null)
                memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    class RemoteImagesLoader implements Runnable {
        RemoteImageToLoad photoToLoad;

        RemoteImagesLoader(RemoteImageToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {

            Bitmap bmp = getWidgetBitmap(photoToLoad.url, photoToLoad.cornerRadius, true, photoToLoad.ratio, photoToLoad.color);
            photoToLoad.imageView.setImageViewBitmap(photoToLoad.id, bmp);
           /* RemoteBitmapDisplayer bd = new RemoteBitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);*/
        }
    }


    public boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }


    //Used to display bitmap in the UI thread
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
                    ((GeneratedImageView)photoToLoad.imageView).onLoaded();
                }
            }
           /* else
                photoToLoad.imageView.setImageResource(stub_id);*/
        }
    }

    class RemoteBitmapDisplayer implements Runnable {
        Bitmap bitmap;
        RemoteImageToLoad photoToLoad;

        public RemoteBitmapDisplayer(Bitmap b, RemoteImageToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {

            if (bitmap != null)
                photoToLoad.imageView.setImageViewBitmap(photoToLoad.id, bitmap);
            //photoToLoad.imageView.setImageBitmap(bitmap);
           /* else
                photoToLoad.imageView.setImageResource(stub_id);*/
        }
    }

    public void clearCache() {
        memoryCache.clear();
        memoryCache2.clear();
        fileCache.clear();
    }

    public void clearWidgetCache() {
        memoryCache2.clear();
    }

}
