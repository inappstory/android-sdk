package com.inappstory.sdk.stories.filedownloader;

public class FileDownloadCallbackAdapter implements IFileDownloadCallback {
    @Override
    public void onSuccess(String fileAbsolutePath) {}

    @Override
    public void onError(int errorCode, String error) {}
}
