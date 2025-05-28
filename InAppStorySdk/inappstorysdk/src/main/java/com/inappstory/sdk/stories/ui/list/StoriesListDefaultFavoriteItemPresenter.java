package com.inappstory.sdk.stories.ui.list;

import android.graphics.Bitmap;


import com.inappstory.sdk.memcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.memcache.IGetBitmap;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCacheError;

import java.util.HashMap;

public class StoriesListDefaultFavoriteItemPresenter implements IStoriesListDefaultFavoriteItemPresenter {

    private final HashMap<Integer, String> localLink = new HashMap<>();

    @Override
    public boolean isSameImageLink(int index, String link) {
        String currentPath = localLink.get(index);
        return currentPath == null || !currentPath.equals(link);
    }

    @Override
    public void storeImageLinkLocal(int index, String link) {
        localLink.put(index, link);
    }

    @Override
    public void getBitmap(final int index, final String link, final IGetBitmap getBitmapCallback) {
        new GetBitmapFromCacheWithFilePath(
                link,
                new IGetBitmapFromMemoryCache() {
                    @Override
                    public void get(final Bitmap bitmap) {
                        storeImageLinkLocal(index, link);
                        getBitmapCallback.onSuccess(bitmap);
                    }
                },
                new IGetBitmapFromMemoryCacheError() {
                    @Override
                    public void onError() {
                        storeImageLinkLocal(index, null);
                        getBitmapCallback.onError();
                    }
                }
        ).get();
    }
}
