package com.inappstory.sdk.core.ui.screens;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;

public final class ShareProcessHandler {
    private OverlapFragmentObserver overlapFragmentObserver;
    private IShareCompleteListener shareCompleteListener = null;
    private Boolean tempShareStatus = null;

    private boolean sharingProcess = false;
    private static final Object shareLock = new Object();

    public boolean isShareProcess() {
        synchronized (shareLock) {
            return sharingProcess;
        }
    }

    public void isShareProcess(boolean sharingProcess) {
        synchronized (shareLock) {
            this.sharingProcess = sharingProcess;
        }
    }

    public static ShareProcessHandler getInstance() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return null;
        return inAppStoryManager.getScreensHolder().getShareProcessHandler();
    }

    public ShareListener shareListener() {
        return shareListener;
    }

    public void shareListener(ShareListener shareListener) {
        this.shareListener = shareListener;
    }

    private ShareListener shareListener;

    public void setTempShareStatus(boolean tempShareStatus) {
        this.tempShareStatus = tempShareStatus;
    }


    public void clearShareIds() {
        shareCompleteListener(null);
    }

    public Boolean getTempShareStatus() {
        Boolean status = tempShareStatus;
        tempShareStatus = null;
        return status;
    }

    public void shareCompleteListener(IShareCompleteListener shareCompleteListener) {
        this.shareCompleteListener = shareCompleteListener;
    }

    public IShareCompleteListener shareCompleteListener() {
        return this.shareCompleteListener;
    }

    public void overlapFragmentObserver(OverlapFragmentObserver overlapFragmentObserver) {
        this.overlapFragmentObserver = overlapFragmentObserver;
    }

    public OverlapFragmentObserver overlapFragmentObserver() {
        return overlapFragmentObserver;
    }
}
