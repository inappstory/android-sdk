package com.inappstory.sdk.stories.filedownloader;

public interface IFileDownloadCallback {
    void onSuccess(String fileAbsolutePath);

    void onError(int errorCode, String error);
}
