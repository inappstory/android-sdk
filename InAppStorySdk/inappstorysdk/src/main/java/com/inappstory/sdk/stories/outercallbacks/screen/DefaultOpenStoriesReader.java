package com.inappstory.sdk.stories.outercallbacks.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.oldreader.StoriesActivity;
import com.inappstory.sdk.stories.ui.oldreader.StoriesDialogFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesMainActivity;
import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.StoriesReaderState;
import com.inappstory.sdk.stories.utils.Sizes;


public class DefaultOpenStoriesReader implements IOpenStoriesReader {

    @Override
    public void onOpen(
            Context context,
            StoriesReaderAppearanceSettings appearanceSettings,
            StoriesReaderLaunchData launchData
    ) {
        if (context == null) return;
        if (Sizes.isTablet(context) && context instanceof FragmentActivity) {
            StoriesDialogFragment storiesDialogFragment = new StoriesDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(
                    launchData.getSerializableKey(),
                    launchData
            );
            bundle.putSerializable(
                    appearanceSettings.getSerializableKey(),
                    appearanceSettings
            );
            storiesDialogFragment.setArguments(bundle);
            try {
                storiesDialogFragment.show(
                        ((FragmentActivity) context).getSupportFragmentManager(),
                        "DialogFragment");
                ScreensManager.getInstance().currentStoriesReaderScreen = storiesDialogFragment;
            } catch (IllegalStateException ignored) {

            }
        } else {
            IStoriesReaderViewModel storiesReaderViewModel = IASUICore.getInstance().getStoriesReaderVM();
            storiesReaderViewModel.initNewState(
                    new StoriesReaderState()
                            .launchData(launchData)
                            .appearanceSettings(appearanceSettings)
            );
            storiesReaderViewModel.openAnimationStatus(true);
            Intent intent2 = new Intent(context, StoriesMainActivity.class);
            /*intent2.putExtra(
                    appearanceSettings.getSerializableKey(),
                    appearanceSettings
            );
            intent2.putExtra(
                    launchData.getSerializableKey(),
                    launchData
            );
            if (!(context instanceof Activity)) {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }*/
            context.startActivity(intent2);
        }
    }
}