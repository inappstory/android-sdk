package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.DownloadResourcesUseCase;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.ArchiveUseCase;
import com.inappstory.sdk.stories.cache.usecases.GameFolderUseCase;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.utils.EmptyProgressCallback;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadGameFilesUseCase {

    private final List<IGameCenterData> gamesData;
    private final DownloadInterruption interruption;
    private final FilesDownloadManager filesDownloadManager;
    private final IASCore core;

    public LoadGameFilesUseCase(
            IASCore core,
            List<IGameCenterData> gamesData,
            FilesDownloadManager filesDownloadManager,
            DownloadInterruption interruption
    ) {
        this.core = core;
        this.gamesData = gamesData;
        this.interruption = interruption;
        this.filesDownloadManager = filesDownloadManager;
    }

    public void download(SuccessUseCaseCallback<IGameCenterData> successUseCaseCallback) {
        for (IGameCenterData data : gamesData) {
            loadGameData(data, successUseCaseCallback);
        }
    }

    private final ExecutorService gameUseCasesThread = Executors.newFixedThreadPool(1);

    private void loadGameData(
            final IGameCenterData data,
            final SuccessUseCaseCallback<IGameCenterData> successUseCaseCallback
    ) {
        if (interruption.active) return;
        if (data.url() == null || data.url().isEmpty()) return;
        final String archiveUrl = data.url();
        final ProgressCallback emptyProgress = new EmptyProgressCallback();
        final long totalArchiveSize;
        final long totalResourcesSize;
        long tempResourcesSize = 0;
        if (data.archiveSize() != null)
            totalArchiveSize = data.archiveSize();
        else
            totalArchiveSize = 0;
        if (data.resources() != null)
            for (WebResource resource : data.resources()) {
                tempResourcesSize += resource.size;
            }
        totalResourcesSize = tempResourcesSize;
        final long finalTotalFilesSize;
        final long finalTotalDownloadsSize = totalArchiveSize + totalResourcesSize;
        if (data.archiveUncompressedSize() != null)
            finalTotalFilesSize = finalTotalDownloadsSize + data.archiveUncompressedSize();
        else
            finalTotalFilesSize = finalTotalDownloadsSize;

        final long[] totalProgress = {0};
        final String[] resourcesHash = {""};
        final String[] gameFolder = {""};
        final DownloadResourcesUseCase downloadResourcesUseCase =
                new DownloadResourcesUseCase(
                        filesDownloadManager,
                        data.resources(),
                        data.id(),
                        archiveUrl,
                        interruption,
                        emptyProgress,
                        new SuccessUseCaseCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                core.statistic().profiling().setReady(resourcesHash[0]);
                                successUseCaseCallback.onSuccess(data);
                            }
                        }
                );


        final GameFolderUseCase gameFolderUseCase = new GameFolderUseCase(
                filesDownloadManager,
                archiveUrl,
                new SuccessUseCaseCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        gameFolder[0] = result;
                        totalProgress[0] += 0.2 * finalTotalDownloadsSize;
                        resourcesHash[0] = core.statistic().profiling().addTask(
                                "game_resources_download"
                        );
                        downloadResourcesUseCase.download();
                    }
                },
                emptyProgress
        );

        final ArchiveUseCase getZipFileUseCase =
                new ArchiveUseCase(
                        core,
                        filesDownloadManager,
                        archiveUrl,
                        data.archiveSize(),
                        data.archiveSha1(),
                        finalTotalFilesSize,
                        emptyProgress,
                        interruption,
                        new SuccessUseCaseCallback<File>() {
                            @Override
                            public void onSuccess(File result) {
                                totalProgress[0] += result.length();
                                gameFolderUseCase.getFile();
                            }
                        }
                );
        gameUseCasesThread.submit(new Runnable() {
            @Override
            public void run() {
                getZipFileUseCase.getFile();
            }
        });
    }
}
