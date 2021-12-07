package com.inappstory.sdk.stories.events;

import com.inappstory.sdk.InAppStoryManager;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class CloseStoryReaderEvent {
    public boolean isOnboardingEvent() {
        return isOnboardingEvent;
    }

    boolean isOnboardingEvent;

    public int getAction() {
        return action;
    }

    int action;

    public CloseStoryReaderEvent() {
        InAppStoryManager.debugSDKCalls("IASManager_closeStoryReader", "");
    }


    public CloseStoryReaderEvent(int action) {
        InAppStoryManager.debugSDKCalls("IASManager_closeStoryReader", "");
        this.action = action;
    }

    public CloseStoryReaderEvent(boolean isOnboardingEvent) {
        InAppStoryManager.debugSDKCalls("IASManager_closeStoryReader", "");
        this.isOnboardingEvent = isOnboardingEvent;
    }
}
