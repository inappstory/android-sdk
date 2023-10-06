package com.inappstory.sdk.stories.uidomain.list.utils;

import com.inappstory.sdk.InAppStoryService;

public class CheckIASServiceWithRetry {
    public void check(CheckIASServiceCallback callback) {
        checkOrRetry(true, callback);
    }

    private void checkOrRetry(boolean retry, CheckIASServiceCallback callback) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) {
            if (retry) {
                checkOrRetry(false, callback);
            } else {
                callback.onError();
            }
        } else {
            callback.onSuccess(service);
        }
    }

}
