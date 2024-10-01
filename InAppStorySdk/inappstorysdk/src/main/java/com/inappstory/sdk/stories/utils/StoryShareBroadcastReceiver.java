package com.inappstory.sdk.stories.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.share.IShareCompleteListener;

public class StoryShareBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                ShareProcessHandler shareProcessHandler = core.screensManager().getShareProcessHandler();
                if (shareProcessHandler == null) return;
                IShareCompleteListener shareCompleteListener = shareProcessHandler.shareCompleteListener();
                if (shareCompleteListener != null) {
                    shareCompleteListener.complete(true);
                }
            }
        });

    }
}
