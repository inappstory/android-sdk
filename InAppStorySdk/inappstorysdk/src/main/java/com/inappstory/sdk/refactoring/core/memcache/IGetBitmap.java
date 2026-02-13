package com.inappstory.sdk.refactoring.core.memcache;

import android.graphics.Bitmap;

public interface IGetBitmap {
    void onSuccess(Bitmap bitmap);

    void onError();
}
