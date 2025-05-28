package com.inappstory.sdk.memcache;

import android.graphics.Bitmap;

import androidx.annotation.MainThread;

public interface IGetBitmapFromMemoryCache {
    @MainThread
    void get(Bitmap bitmap);
}
