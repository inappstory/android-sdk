package com.inappstory.sdk;


import static com.inappstory.sdk.InAppStoryManager.IAS_DEBUG_API;

import android.annotation.SuppressLint;

@SuppressLint(IAS_DEBUG_API)
public interface IASLogger {
    void showELog(String tag, String message);

    void showDLog(String tag, String message);
}
