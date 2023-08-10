package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;

public interface FileLoadProgressCallback extends ProgressCallback {
    void onSuccess(File file);
    void onError(String error);
}
