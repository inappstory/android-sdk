package com.inappstory.sdk.memcache;

import android.graphics.Bitmap;

public interface IGetBitmap {
    void onSuccess(Bitmap bitmap);

    void onError();
}
