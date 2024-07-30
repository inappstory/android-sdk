package com.inappstory.sdk;

import static com.inappstory.sdk.InAppStoryManager.IAS_DEBUG_API;

import android.annotation.SuppressLint;

public class UserDebugLogManager {
    public void setLogger(IASLogger logger) {
        this.logger = logger;
    }

    @SuppressLint(IAS_DEBUG_API)
    private IASLogger logger = new DefaultIASLogger();

    @SuppressLint(IAS_DEBUG_API)
    public void showDLog(String tag, String message) {
        logger.showDLog(tag, message);
    }

    @SuppressLint(IAS_DEBUG_API)
    public void showELog(String tag, String message) {
        logger.showELog(tag, message);
    }

}
