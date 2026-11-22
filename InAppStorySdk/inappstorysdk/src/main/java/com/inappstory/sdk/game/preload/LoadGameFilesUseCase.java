package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.DownloadResourcesUseCase;
import com.inappstory.sdk.game.cache.GameNameHolder;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.GameArchiveItem;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.usecases.ArchiveUseCase;
import com.inappstory.sdk.stories.cache.usecases.GameFolderUseCase;
import com.inappstory.sdk.utils.EmptyProgressCallback;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadGameFilesUseCase {

    private final List<IGameCenterData> gamesData;
    private final DownloadInterruption interruption;
    private final IASCore core;
    private final FileChecker fileChecker;

    public LoadGameFilesUseCase(
            IASCore core,
            FileChecker fileChecker,
            List<IGameCenterData> gamesData,
            DownloadInterruption interruption
    ) {
        this.core = core;
        this.fileChecker = fileChecker;
        this.gamesData = gamesData;
        this.interruption = interruption;
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
        final List<GameArchiveItem> archiveItems = data.archiveItems();
        final String archiveUrl = data.url();
        final String gameName = new GameNameHolder().getGameName(archiveUrl);
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
                        core,
                        fileChecker,
                        data.resources(),
                        data.id(),
                        archiveUrl,
                        gameName,
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
                core,
                archiveUrl,
                gameName,
                fileChecker,
                archiveItems,
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
                        archiveUrl,
                        gameName,
                        fileChecker,
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
                        },
                        true
                );
        gameUseCasesThread.submit(new Runnable() {
            @Override
            public void run() {
                getZipFileUseCase.getFile();
            }
        });
    }
}
