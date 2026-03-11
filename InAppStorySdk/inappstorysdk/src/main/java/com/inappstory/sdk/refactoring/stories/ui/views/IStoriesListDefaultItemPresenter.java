package com.inappstory.sdk.refactoring.stories.ui.views;

import com.inappstory.sdk.memcache.IGetBitmap;

public interface IStoriesListDefaultItemPresenter {

    void getBitmap(String link, IGetBitmap getBitmapCallback);
}