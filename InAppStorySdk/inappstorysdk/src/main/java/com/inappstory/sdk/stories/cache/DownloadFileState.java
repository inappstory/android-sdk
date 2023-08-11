package com.inappstory.sdk.stories.cache;

import java.io.File;

public class DownloadFileState {
    public DownloadFileState(File file, long totalSize, long downloadedSize) {
        this.file = file;
        this.totalSize = totalSize;
        this.downloadedSize = downloadedSize;
    }

    public File file;
    public long totalSize;
    public long downloadedSize;

    public File getFullFile() {
        if (downloadedSize == totalSize && file.exists())
            return file;
        return null;
    }
}
