package com.inappstory.sdk.refactoring.core.memcache;

import android.graphics.Bitmap;

import androidx.annotation.MainThread;

public interface IGetBitmapFromMemoryCache {
    @MainThread
    void get(Bitmap bitmap);
}
