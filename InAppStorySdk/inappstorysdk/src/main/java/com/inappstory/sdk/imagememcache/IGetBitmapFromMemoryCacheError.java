package com.inappstory.sdk.imagememcache;

import androidx.annotation.MainThread;

public interface IGetBitmapFromMemoryCacheError {
    @MainThread
    void onError();
}
