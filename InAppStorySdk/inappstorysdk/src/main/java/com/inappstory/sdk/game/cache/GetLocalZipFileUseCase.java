package com.inappstory.sdk.game.cache;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;

import java.io.File;
import java.io.IOException;

public class GetLocalZipFileUseCase {
    String url;
    long size;
    String sha1;

    public GetLocalZipFileUseCase(String url,
                                  long size,
                                  String sha1) {
        this.url = url;
        this.sha1 = sha1;
        this.size = size;
    }

    void get(
             @NonNull LruDiskCache cache,
            UseCaseCallback<File> callback
    ) {
        FileChecker fileChecker = new FileChecker();
        File cachedArchive = cache.getFullFile(url);
        if (cachedArchive != null) {
            if (!fileChecker.checkWithShaAndSize(
                    cachedArchive,
                    size,
                    sha1,
                    true
            )) {
                File directory = new File(
                        cachedArchive.getParent() +
                                File.separator + url.hashCode());
                try {
                    cache.delete(url);
                    if (directory.exists()) {
                        FileManager.deleteRecursive(directory);
                    }
                } catch (Exception e) {

                }
                callback.onError(null);
            } else {
                callback.onSuccess(cachedArchive);
            }
        }
        callback.onError(null);
    }
}