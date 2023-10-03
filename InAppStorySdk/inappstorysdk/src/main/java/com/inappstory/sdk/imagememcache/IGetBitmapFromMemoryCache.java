package com.inappstory.sdk.imagememcache;

import android.graphics.Bitmap;

import androidx.annotation.MainThread;

public interface IGetBitmapFromMemoryCache {
    @MainThread
    void get(Bitmap bitmap);
}
