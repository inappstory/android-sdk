package com.inappstory.sdk.stories.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.core.ui.screens.ScreensManager;

public class StoryShareBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ShareProcessHandler shareProcessHandler = ShareProcessHandler.getInstance();
        if (shareProcessHandler == null) return;
        IShareCompleteListener shareCompleteListener = shareProcessHandler.shareCompleteListener();
        if (shareCompleteListener != null) {
            shareCompleteListener.complete(true);
        }
    }
}
