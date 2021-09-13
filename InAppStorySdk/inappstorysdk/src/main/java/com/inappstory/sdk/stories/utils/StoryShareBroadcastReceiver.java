package com.inappstory.sdk.stories.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesFixedActivity;

import static android.content.Intent.EXTRA_CHOSEN_COMPONENT;

public class StoryShareBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName clickedComponent = intent.getParcelableExtra(EXTRA_CHOSEN_COMPONENT);
        if (clickedComponent != null && ScreensManager.getInstance().getTempShareId() != null) {
            if (ScreensManager.getInstance().currentGameActivity != null) {
                ScreensManager.getInstance().currentGameActivity.shareComplete(
                        ScreensManager.getInstance().getTempShareId(),
                        true);
            } else if (ScreensManager.getInstance().currentActivity instanceof StoriesActivity) {
                ((StoriesActivity) ScreensManager.getInstance().currentActivity).shareComplete();
            } else if (ScreensManager.getInstance().currentActivity instanceof StoriesFixedActivity) {
                ((StoriesFixedActivity) ScreensManager.getInstance().currentActivity).shareComplete();
            }
          /*  CsEventBus.getDefault().post(new ShareCompleteEvent(
                    ScreensManager.getInstance().getTempShareStoryId(),
                    ScreensManager.getInstance().getTempShareId(),
                    true));*/


        }
    }
}
