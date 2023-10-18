package com.inappstory.sdk.core.imagememcache;

import android.graphics.Bitmap;

import androidx.annotation.MainThread;

public interface IGetBitmapFromMemoryCache {
    @MainThread
    void get(Bitmap bitmap);
}
