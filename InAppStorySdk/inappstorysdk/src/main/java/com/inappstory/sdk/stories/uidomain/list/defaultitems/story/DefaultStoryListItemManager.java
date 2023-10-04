package com.inappstory.sdk.stories.uidomain.list.defaultitems.story;

import android.graphics.Bitmap;

import com.inappstory.sdk.imagememcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.imagememcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.imagememcache.IGetBitmapFromMemoryCacheError;
import com.inappstory.sdk.stories.uidomain.list.defaultitems.IGetBitmap;

public class DefaultStoryListItemManager implements IDefaultStoryListItemManager {
    @Override
    public void getBitmap(String link, final IGetBitmap getBitmapCallback) {
        new GetBitmapFromCacheWithFilePath(
                link,
                new IGetBitmapFromMemoryCache() {
                    @Override
                    public void get(Bitmap bitmap) {
                        getBitmapCallback.onSuccess(bitmap);
                    }
                },
                new IGetBitmapFromMemoryCacheError() {
                    @Override
                    public void onError() {
                        getBitmapCallback.onError();
                    }
                }
        ).get();
    }
}
