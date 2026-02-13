package com.inappstory.sdk.refactoring.core.memcache;

import androidx.annotation.MainThread;

public interface IGetBitmapFromMemoryCacheError {
    @MainThread
    void onError();
}
