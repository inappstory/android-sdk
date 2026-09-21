package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;

import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesTabletActivity;
import com.inappstory.sdk.stories.utils.ActivityUtils;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StatusBarController;


public class DefaultOpenStoriesReader implements IOpenStoriesReader {

    @Override
    public void onOpen(
            Context context,
            Bundle bundle
    ) {
        if (context == null) return;
        Intent intent2;
        if (Sizes.isTablet(context)) {
            intent2 = new Intent(context, StoriesTabletActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        } else {
            intent2 = new Intent(context, StoriesActivity.class);
        }
        if (!(context instanceof Activity)) {
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent2.putExtras(bundle);
        context.startActivity(intent2);
        if (Sizes.isTablet(context) && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


    @Override
    public void onHideStatusBar(Context context) {
        if (context instanceof Activity)
            StatusBarController.showFullscreen((Activity) context);
    }

    @Override
    public void onRestoreStatusBar(Context context) {
        if (context instanceof Activity)
            StatusBarController.restore((Activity) context);
    }

    @Override
    public void onShowInFullscreen(Context context) {
        if (context instanceof Activity)
            StatusBarController.showFullscreen((Activity) context);
    }

    @Override
    public void onRestoreScreen(Context context) {
        if (context instanceof Activity)
            StatusBarController.restore((Activity) context);
    }
}
