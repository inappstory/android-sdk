package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.memcache.IGetBitmap;

public interface IStoriesListDefaultItemPresenter {

    void getBitmap(String link, IGetBitmap getBitmapCallback);
}