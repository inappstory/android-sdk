package com.inappstory.sdk.stories.ui.list;

import android.graphics.Bitmap;

import com.inappstory.sdk.memcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.memcache.IGetBitmap;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCacheError;

public class StoriesListDefaultItemPresenter implements IStoriesListDefaultItemPresenter {
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
