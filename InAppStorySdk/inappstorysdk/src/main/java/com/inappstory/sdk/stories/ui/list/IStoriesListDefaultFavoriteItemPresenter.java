package com.inappstory.sdk.stories.ui.list;


import com.inappstory.sdk.memcache.IGetBitmap;

public interface IStoriesListDefaultFavoriteItemPresenter {
    boolean isSameImageLink(int index, String link);

    void storeImageLinkLocal(int index, String link);

    void getBitmap(int index, String link, IGetBitmap getBitmapCallback);
}
