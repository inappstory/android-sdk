package com.inappstory.sdk.stories.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.stories.events.ShareCompleteEvent;
import com.inappstory.sdk.stories.ui.ScreensManager;

import static android.content.Intent.EXTRA_CHOSEN_COMPONENT;

public class StoryShareBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName clickedComponent = intent.getParcelableExtra(EXTRA_CHOSEN_COMPONENT);
        if (clickedComponent != null && ScreensManager.getInstance().getTempShareId() != null) {
            CsEventBus.getDefault().post(new ShareCompleteEvent(
                    ScreensManager.getInstance().getTempShareStoryId(),
                    ScreensManager.getInstance().getTempShareId(),
                    true));
        }
    }
}
