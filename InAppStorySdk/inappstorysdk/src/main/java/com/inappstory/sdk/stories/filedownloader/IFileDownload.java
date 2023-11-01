package com.inappstory.sdk.stories.filedownloader;


public interface IFileDownload extends
        ICacheSettings,
        IInterruptionHolder,
        IFileDownloadProgressCallback {
    void downloadOrGetFromCache() throws Exception;

    String getFromCache() throws Exception;

    FileDownload addDownloadCallback(IFileDownloadCallback callback);
}
