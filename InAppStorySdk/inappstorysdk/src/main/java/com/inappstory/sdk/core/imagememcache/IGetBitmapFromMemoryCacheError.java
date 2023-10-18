package com.inappstory.sdk.core.imagememcache;

import androidx.annotation.MainThread;

public interface IGetBitmapFromMemoryCacheError {
    @MainThread
    void onError();
}
