package com.inappstory.sdk.stories.cache;

import androidx.annotation.NonNull;

import java.io.File;

public class DownloadFileState {
    public DownloadFileState(File file, long totalSize, long downloadedSize) {
        this.file = file;
        this.downloadedSize = downloadedSize;
        if (downloadedSize > totalSize)
            this.totalSize = -1;
        else
            this.totalSize = totalSize;
    }

    public DownloadFileState(boolean waiting) {
        this.waiting = waiting;
    }

    public File file;
    public long totalSize;
    public long downloadedSize;
    private boolean waiting = false;

    public boolean waiting() {
        return waiting;
    }

    public File getFullFile() {
        if (isFullDownload())
            return file;
        return null;
    }

    public boolean isFullDownload() {
        return downloadedSize == totalSize && file.exists();
    }

    @NonNull
    @Override
    public String toString() {
        if (file == null) return "No file";
        return file.getAbsolutePath();
    }
}
