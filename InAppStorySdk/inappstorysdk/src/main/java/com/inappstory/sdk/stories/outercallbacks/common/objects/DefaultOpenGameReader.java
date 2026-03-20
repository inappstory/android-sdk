package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import com.inappstory.sdk.R;
import com.inappstory.sdk.game.reader.GameActivity;
import com.inappstory.sdk.stories.utils.ActivityUtils;
import com.inappstory.sdk.stories.utils.Sizes;


public class DefaultOpenGameReader implements IOpenGameReader {

    @Override
    public void onOpen(
            Context context,
            Bundle bundle
    ) {
        if (context == null) return;
        Intent intent2 = new Intent(context, GameActivity.class);
        if (context instanceof Activity) {

            Window window = ((Activity) context).getWindow();
            Integer themeId = ActivityUtils.getThemeResId((Activity) context);
            bundle.putInt("themeId",
                    ((Activity) context).getIntent().getIntExtra(
                            "themeId",
                            themeId != null ? themeId : R.style.StoriesSDKAppTheme_GameActivity
                    )
            );
            bundle.putInt("parentSystemUIVisibility",
                    ((Activity) context).getIntent().getIntExtra(
                            "parentSystemUIVisibility",
                            window.getDecorView().getSystemUiVisibility()
                    )
            );
            Pair<Integer, Integer> startedOffsets = getStartedOffsets(context);
            bundle.putInt("startedTop", startedOffsets.first);
            bundle.putInt("startedBottom", startedOffsets.second);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bundle.putInt("parentLayoutInDisplayCutoutMode",
                        ((Activity) context).getIntent().getIntExtra(
                                "parentLayoutInDisplayCutoutMode",
                                window.getAttributes().layoutInDisplayCutoutMode
                        )
                );
            }
        }
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

    private Pair<Integer, Integer> getStartedOffsets(Context context) {
        int topInsetOffset = 0;
        int bottomInsetOffset = 0;
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
            if (!Sizes.isTablet(activity)) {
                if (Build.VERSION.SDK_INT >= 28) {
                    if (activity.getWindow() != null) {
                        WindowInsets windowInsets = activity.getWindow().getDecorView().getRootWindowInsets();
                        if (windowInsets != null) {
                            topInsetOffset = Math.max(0, windowInsets.getStableInsetTop());
                            bottomInsetOffset = Math.max(0, windowInsets.getStableInsetBottom());
                        }
                    }
                }
            }
        }
        return new Pair<>(topInsetOffset, bottomInsetOffset);
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
