package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.R;
import com.inappstory.sdk.game.reader.GameActivity;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
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
            Intent currentActivityIntent = ((Activity) context).getIntent();
            if (currentActivityIntent != null) {
                bundle.putInt("themeId",
                        currentActivityIntent.getIntExtra(
                                "themeId",
                                themeId != null ? themeId : R.style.StoriesSDKAppTheme_GameActivity
                        )
                );
                bundle.putInt("parentSystemUIVisibility",
                        currentActivityIntent.getIntExtra(
                                "parentSystemUIVisibility",
                                window.getDecorView().getSystemUiVisibility()
                        )
                );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bundle.putInt("parentLayoutInDisplayCutoutMode",
                            currentActivityIntent.getIntExtra(
                                    "parentLayoutInDisplayCutoutMode",
                                    window.getAttributes().layoutInDisplayCutoutMode
                            )
                    );
                }
            } else {
                bundle.putInt("themeId",
                        themeId != null ? themeId : R.style.StoriesSDKAppTheme_GameActivity
                );
                bundle.putInt("parentSystemUIVisibility",
                        window.getDecorView().getSystemUiVisibility()
                );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bundle.putInt("parentLayoutInDisplayCutoutMode",
                            window.getAttributes().layoutInDisplayCutoutMode
                    );
                }
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
