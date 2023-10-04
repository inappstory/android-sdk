package com.inappstory.sdk.stories.uidomain.list.defaultitems;

import android.graphics.Bitmap;

public interface IGetBitmap {
    void onSuccess(Bitmap bitmap);

    void onError();
}
