package com.inappstory.sdk.memcache;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;


import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetBitmapFromCacheWithFilePath {

    public GetBitmapFromCacheWithFilePath(
            @NonNull String filePath,
            IGetBitmapFromMemoryCache cacheSuccess,
            IGetBitmapFromMemoryCacheError cacheError
    ) {
        if (cacheSuccess == null) {
            this.cacheSuccess = new IGetBitmapFromMemoryCache() {
                @Override
                public void get(Bitmap bitmap) {

                }
            };
        } else {
            this.cacheSuccess = cacheSuccess;
        }
        if (cacheError == null) {
            this.cacheError = new IGetBitmapFromMemoryCacheError() {
                @Override
                public void onError() {

                }
            };
        } else {
            this.cacheError = cacheError;
        }
        this.filePath = filePath;
    }

    private final String filePath;
    private final IGetBitmapFromMemoryCache cacheSuccess;
    private final IGetBitmapFromMemoryCacheError cacheError;
    private static final ExecutorService fileSystemThread = Executors.newFixedThreadPool(1);
    private static final BitmapCacheHolder bitmapCacheHolder = new BitmapCacheHolder();

    public void get() {
        Bitmap bitmap = bitmapCacheHolder.getBitmapFromCache(filePath);
        if (bitmap != null) {
            cacheSuccess.get(bitmap);
        } else {
            fileSystemThread.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap result = (new GetBitmapFromFilePath(filePath)).call();
                        bitmapCacheHolder.addBitmapToCache(filePath, result);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null) {
                                    cacheSuccess.get(result);
                                } else {
                                    cacheError.onError();
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            });
        }
    }
}
