package com.inappstory.sdk.core.dataholders;

import android.graphics.Bitmap;

import com.inappstory.sdk.core.data.IListItemContent;

public interface IListsContentHolder extends IHolderWithContentTypes<IListItemContent> {
    String getPathByUrl(String url);
    Bitmap getBitmapByPath(String path);
    void setBitmapByPath(String path, Bitmap bitmap);
    void setPathByUrl(String url, String path);
}
