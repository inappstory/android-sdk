package com.inappstory.sdk.stories.uidomain.list.defaultitems.story;

import android.graphics.Bitmap;

import com.inappstory.sdk.core.utils.imagememcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.core.utils.imagememcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.core.utils.imagememcache.IGetBitmapFromMemoryCacheError;
import com.inappstory.sdk.stories.uidomain.list.defaultitems.IGetBitmap;

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
