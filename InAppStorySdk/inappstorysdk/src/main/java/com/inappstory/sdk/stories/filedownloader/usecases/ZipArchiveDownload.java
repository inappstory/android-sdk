package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;


import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.stories.filedownloader.ProgressFileDownload;

import java.io.File;

public final class ZipArchiveDownload extends ProgressFileDownload {
    String zipArchiveName;
    DownloadInterruption interruption;

    public ZipArchiveDownload(
            @NonNull String url,
            @NonNull String zipArchiveName,
            final Long size,
            final String sha,
            final Long totalFilesSize,
            @NonNull LruDiskCache cache,
            @NonNull DownloadInterruption interruption
    ) {
        super(url, size, sha, totalFilesSize, cache);
        this.zipArchiveName = zipArchiveName;
        this.interruption = interruption;
    }

    @Override
    public String getCacheKey() {
        return deleteQueryArgumentsFromUrl(url);
    }

    @Override
    public String getDownloadFilePath() {
        File zipDir = new File(
                cache.getCacheDir() +
                        File.separator + "zip" +
                        File.separator + zipArchiveName +
                        File.separator
        );
        if (!zipDir.getAbsolutePath().startsWith(
                cache.getCacheDir() +
                        File.separator + "zip")) {
            return null;
        }
        File zipFile = new File(zipDir, url.hashCode() + ".zip");
        return zipFile.getAbsolutePath();
    }


    @Override
    public boolean isInterrupted() {
        return interruption.active;
    }

}
