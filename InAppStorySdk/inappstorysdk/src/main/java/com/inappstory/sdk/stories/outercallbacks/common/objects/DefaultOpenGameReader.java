package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.game.reader.GameActivity;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
import com.inappstory.sdk.stories.utils.Sizes;


public class DefaultOpenGameReader implements IOpenGameReader {

    @Override
    public void onOpen(
            Context context,
            Bundle bundle

    ) {
        if (context == null) return;
        Intent intent2 = new Intent(context, GameActivity.class);
        intent2.putExtras(bundle);
        if (context instanceof Activity) {
            ((Activity) context).startActivity(intent2);
            ((Activity) context).overridePendingTransition(0, 0);
        } else {
            try {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            } catch (Exception e) {
            }
        }
    }
}
