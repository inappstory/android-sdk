package com.inappstory.sdk.game.cache;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.lrudiskcache.FileChecker;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;

public class GetZipFileUseCase extends GameNameHolder {
    String url;

    long size;
    String sha1;

    public GetZipFileUseCase(String url,
                             long size,
                             String sha1) {
        this.url = url;
        this.sha1 = sha1;
        this.size = size;
    }

    @WorkerThread
    void get() {
    }

    @WorkerThread
    void get(
            final DownloadInterruption interruption,
            final @NonNull UseCaseCallback<File> callback,
            final ProgressCallback progressCallback,
            final long totalGameSize
    ) {
        final String hash = randomUUID().toString();
        String gameName = getGameName(url);
        ProfilingManager.getInstance().addTask("game_download", hash);
        IASCore.getInstance().filesRepository.getZipArchive(
                url,
                gameName,
                size,
                sha1,
                totalGameSize,
                new IFileDownloadCallback() {
                    @Override
                    public void onSuccess(String fileAbsolutePath) {
                        ProfilingManager.getInstance().setReady(hash);
                        File file = new File(fileAbsolutePath);
                        callback.onSuccess(file);
                    }

                    @Override
                    public void onError(int errorCode, String error) {
                        ProfilingManager.getInstance().setReady(hash);
                        callback.onError(error);
                    }
                },
                new IFileDownloadProgressCallback() {
                    @Override
                    public void onProgress(long currentProgress, long max) {
                        progressCallback.onProgress(currentProgress, max);
                    }
                },
                interruption
        );
    }


}
