package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.inappstory.sdk.R;
import com.inappstory.sdk.inappmessage.ui.reader.IAMActivity;
import com.inappstory.sdk.stories.utils.ActivityUtils;


public class DefaultOpenInAppMessageReader implements IOpenInAppMessageReader {

    @Override
    public void onOpen(
            Context context,
            Bundle bundle
    ) {
        if (context == null) return;
        Intent intent2 = new Intent(context, IAMActivity.class);
        if (context instanceof Activity) {

            Window window = ((Activity) context).getWindow();
            Integer themeId = ActivityUtils.getThemeResId((Activity) context);
            bundle.putInt("themeId",
                    ((Activity) context).getIntent().getIntExtra(
                            "themeId",
                            themeId != null ? themeId : R.style.StoriesSDKAppTheme_InAppMessageActivity
                    )
            );
            bundle.putInt("parentSystemUIVisibility",
                    ((Activity) context).getIntent().getIntExtra(
                            "parentSystemUIVisibility",
                            window.getDecorView().getSystemUiVisibility()
                    )
            );
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
