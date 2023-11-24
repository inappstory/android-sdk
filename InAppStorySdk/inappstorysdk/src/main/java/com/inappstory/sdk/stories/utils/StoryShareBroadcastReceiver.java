package com.inappstory.sdk.stories.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.ui.ScreensManager;

public class StoryShareBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        IShareCompleteListener shareCompleteListener = ScreensManager.getInstance().shareCompleteListener();
        if (shareCompleteListener != null) {
            shareCompleteListener.complete(true);
        }
    }
}
