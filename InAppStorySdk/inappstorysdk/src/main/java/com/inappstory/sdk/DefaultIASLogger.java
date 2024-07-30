package com.inappstory.sdk;

import static com.inappstory.sdk.InAppStoryManager.IAS_DEBUG_API;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint(IAS_DEBUG_API)
public class DefaultIASLogger implements IASLogger {
    @Override
    public void showELog(String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void showDLog(String tag, String message) {
        Log.d(tag, message);
    }
}
