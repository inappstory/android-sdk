package com.inappstory.sdk.stories.ui.reader;

import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.callbacks.OverlappingContainerActions;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.ScreensManager;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class OverlappingFragmentContainerActions implements OverlappingContainerActions {
    private WeakReference<OverlapFragment> fragmentWeakReference;

    public OverlappingFragmentContainerActions(OverlapFragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    public void closeView(HashMap<String, Object> data) {
        OverlapFragment fragment = fragmentWeakReference.get();
        boolean shared = false;
        if (data.containsKey("shared")) shared = (boolean) data.get("shared");

        OverlapFragmentObserver observer = ScreensManager.getInstance().overlapFragmentObserver;
        if (observer != null) observer.closeView(data);
        ScreensManager.getInstance().cleanOverlapFragmentObserver();
        if (fragment != null) {
            try {
                fragment.getParentFragmentManager().popBackStack();
            } catch (IllegalStateException e) {
                ScreensManager.getInstance().setTempShareStatus(shared);
            }
            ShareListener shareListener = fragment.shareListenerWeakReference.get();
            if (shareListener != null) {
                if (shared)
                    shareListener.onSuccess(true);
                else
                    shareListener.onCancel();
            }
        }

    }
}
