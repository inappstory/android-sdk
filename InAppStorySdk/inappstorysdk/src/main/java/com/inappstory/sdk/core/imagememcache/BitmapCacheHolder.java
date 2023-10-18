package com.inappstory.sdk.core.imagememcache;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCacheHolder {
    private LruCache<String, Bitmap> memoryCache;

    BitmapCacheHolder() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    void addBitmapToCache(String key, Bitmap bitmap) {
        memoryCache.put(key, bitmap);
    }

    Bitmap getBitmapFromCache(String key) {
        return memoryCache.get(key);
    }
}
