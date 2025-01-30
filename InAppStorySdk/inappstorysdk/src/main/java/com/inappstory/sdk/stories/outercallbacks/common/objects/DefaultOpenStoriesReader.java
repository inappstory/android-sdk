package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
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
        if (Sizes.isTablet(context) && context instanceof FragmentActivity) {
            Intent intent2 = new Intent(context, StoriesTabletActivity.class);
            if (!(context instanceof Activity)) {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent2.putExtras(bundle);
            context.startActivity(intent2);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }


           /* final StoriesDialogFragment storiesDialogFragment = new StoriesDialogFragment();
            storiesDialogFragment.setArguments(bundle);
            try {
                storiesDialogFragment.show(
                        ((FragmentActivity) context).getSupportFragmentManager(),
                        "DialogFragment");
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        core.screensManager().getStoryScreenHolder().subscribeScreen(storiesDialogFragment);
                    }
                });
            } catch (IllegalStateException ignored) {

            }*/
        } else {
            Intent intent2 = new Intent(context, StoriesActivity.class);
            if (!(context instanceof Activity)) {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent2.putExtras(bundle);
            context.startActivity(intent2);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }


    @Override
    public void onHideStatusBar(Context context) {
        if (context instanceof Activity)
            StatusBarController.hideStatusBar((Activity) context, true);
    }

    @Override
    public void onRestoreStatusBar(Context context) {
        if (context instanceof Activity)
            StatusBarController.showStatusBar((Activity) context);
    }

    @Override
    public void onShowInFullscreen(Context context) {

    }

    @Override
    public void onRestoreScreen(Context context) {

    }
}
