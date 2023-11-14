package com.inappstory.sdk.core.utils.imagememcache;

import androidx.annotation.MainThread;

public interface IGetBitmapFromMemoryCacheError {
    @MainThread
    void onError();
}
