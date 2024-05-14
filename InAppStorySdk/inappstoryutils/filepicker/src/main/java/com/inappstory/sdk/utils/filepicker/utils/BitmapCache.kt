package com.inappstory.sdk.utils.filepicker.utils

import android.graphics.Bitmap
import androidx.collection.LruCache

class BitmapCache(maxSize: Int) : LruCache<String?, Bitmap?>(maxSize) {
    override fun sizeOf(key: String, bitmap: Bitmap): Int {
        return bitmap.byteCount / 1024
    }
}