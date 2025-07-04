package com.inappstory.sdk.core.ui.screens;

import com.inappstory.sdk.share.IShareCompleteListener;
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
