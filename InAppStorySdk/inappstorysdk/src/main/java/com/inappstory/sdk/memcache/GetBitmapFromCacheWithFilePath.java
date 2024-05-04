package com.inappstory.sdk.memcache;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetBitmapFromCacheWithFilePath {

    public GetBitmapFromCacheWithFilePath(
            String filePath,
            IGetBitmapFromMemoryCache cacheSuccess,
            IGetBitmapFromMemoryCacheError cacheError
    ) {
        this.cacheSuccess = cacheSuccess;
        this.cacheError = cacheError;
        this.filePath = filePath;
    }

    public GetBitmapFromCacheWithFilePath(
            String filePath,
            IGetBitmapFromMemoryCache cacheSuccess
    ) {
        this.cacheSuccess = cacheSuccess;
        this.cacheError = new IGetBitmapFromMemoryCacheError() {
            @Override
            public void onError() {

            }
        };
        this.filePath = filePath;
    }

    public GetBitmapFromCacheWithFilePath(
            String filePath
    ) {
        this.cacheSuccess = new IGetBitmapFromMemoryCache() {
            @Override
            public void get(Bitmap bitmap) {

            }
        };
        this.cacheError = new IGetBitmapFromMemoryCacheError() {
            @Override
            public void onError() {

            }
        };
        this.filePath = filePath;
    }

    private String filePath;
    private IGetBitmapFromMemoryCache cacheSuccess;
    private IGetBitmapFromMemoryCacheError cacheError;
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
