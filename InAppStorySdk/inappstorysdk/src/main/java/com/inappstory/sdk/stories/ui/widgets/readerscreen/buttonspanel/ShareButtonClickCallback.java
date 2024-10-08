package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.view.View;

import java.lang.ref.WeakReference;

public class ShareButtonClickCallback implements ButtonClickCallback {
    private final ButtonsPanelManager manager;
    private WeakReference<View> shareRef = null;

    public ShareButtonClickCallback(ButtonsPanelManager manager, View share) {
        this.manager = manager;
        shareRef = new WeakReference<>(share);
    }

    void onClick() {
        manager.getPageManager().pauseSlide(false, true);
    }

    @Override
    public void onSuccess(int val) {

    }

    @Override
    public void onError() {
        final View share = shareRef.get();
        if (share != null) {
            share.post(new Runnable() {
                @Override
                public void run() {
                    share.setEnabled(true);
                    share.setClickable(true);
                }
            });
        }

    }
}