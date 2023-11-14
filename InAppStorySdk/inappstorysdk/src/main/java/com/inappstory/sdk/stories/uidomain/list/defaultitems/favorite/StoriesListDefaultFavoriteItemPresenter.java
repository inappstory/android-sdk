package com.inappstory.sdk.stories.uidomain.list.defaultitems.favorite;

import android.graphics.Bitmap;

import com.inappstory.sdk.core.utils.imagememcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.core.utils.imagememcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.core.utils.imagememcache.IGetBitmapFromMemoryCacheError;
import com.inappstory.sdk.stories.uidomain.list.defaultitems.IGetBitmap;

import java.util.HashMap;

public class StoriesListDefaultFavoriteItemPresenter implements IStoriesListDefaultFavoriteItemPresenter {

    private HashMap<Integer, String> localLink = new HashMap<>();

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
