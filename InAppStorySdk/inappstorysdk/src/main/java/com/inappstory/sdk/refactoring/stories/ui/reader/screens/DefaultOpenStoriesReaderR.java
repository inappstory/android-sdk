package com.inappstory.sdk.refactoring.stories.ui.reader.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;
import com.inappstory.sdk.stories.utils.ActivityUtils;

public class DefaultOpenStoriesReaderR implements IOpenStoriesReader {

    @Override
    public void onOpen(
            Context context,
            Bundle bundle
    ) {
        if (context == null) return;
        if (context instanceof Activity) {
            Window window = ((Activity) context).getWindow();
            Integer themeId = ActivityUtils.getThemeResId((Activity) context);
            bundle.putInt("themeId", themeId != null ? themeId : R.style.StoriesSDKAppTheme_Transparent);
            bundle.putInt("parentSystemUIVisibility",
                    window.getDecorView().getSystemUiVisibility()
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bundle.putInt("parentLayoutInDisplayCutoutMode", window.getAttributes().layoutInDisplayCutoutMode);
            }
        }
        Intent intent2 = new Intent(context, StoryReaderActivity.class);
        if (!(context instanceof Activity)) {
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent2.putExtras(bundle);
        context.startActivity(intent2);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


    @Override
    public void onHideStatusBar(Context context) {

    }

    @Override
    public void onRestoreStatusBar(Context context) {

    }

    @Override
    public void onShowInFullscreen(Context context) {

    }

    @Override
    public void onRestoreScreen(Context context) {

    }
}
