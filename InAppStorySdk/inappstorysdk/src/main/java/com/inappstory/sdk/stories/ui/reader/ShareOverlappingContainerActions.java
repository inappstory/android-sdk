package com.inappstory.sdk.stories.ui.reader;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.callbacks.OverlappingContainerActions;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;

import java.util.HashMap;

public class ShareOverlappingContainerActions implements OverlappingContainerActions {

    FragmentManager fragmentManager;

    public ShareOverlappingContainerActions(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void closeView(final HashMap<String, Object> data) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                ShareProcessHandler shareProcessHandler =
                        core.screensManager().getShareProcessHandler();
                if (shareProcessHandler == null) return;
                boolean shared = false;
                if (data.containsKey("shared")) shared = (boolean) data.get("shared");
                OverlapFragmentObserver observer = shareProcessHandler.overlapFragmentObserver();
                if (observer != null) observer.closeView(data);
                shareProcessHandler.overlapFragmentObserver(null);
                try {
                    if (fragmentManager != null) fragmentManager.popBackStack();
                    fragmentManager = null;
                } catch (IllegalStateException e) {
                    shareProcessHandler.setTempShareStatus(shared);
                }
                IShareCompleteListener shareCompleteListener = shareProcessHandler.shareCompleteListener();
                if (shareCompleteListener != null) {
                    shareCompleteListener.complete(shared);
                }
            }
        });
    }
}
