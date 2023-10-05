package com.inappstory.sdk.stories.filedownloader;

import java.util.List;

public interface IFilesDownloadCallback {
    void onSuccess(List<String> filesAbsolutePaths);

    void onError(String error);
}
