package com.inappstory.sdk.stories.uidomain.list.utils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;

public interface CheckIASServiceCallback {
    void onSuccess(@NonNull InAppStoryService service);

    void onError();
}
