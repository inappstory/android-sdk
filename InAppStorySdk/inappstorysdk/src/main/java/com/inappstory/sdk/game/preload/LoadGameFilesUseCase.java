package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.game.cache.DownloadResourcesUseCase;
import com.inappstory.sdk.game.cache.GetZipFileUseCase;
import com.inappstory.sdk.game.cache.RemoveOldGameFilesUseCase;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.game.cache.UnzipUseCase;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadGameFilesUseCase {

    private final List<IGameCenterData> gamesData;
    private final DownloadInterruption interruption;
    private final FilesDownloadManager filesDownloadManager;


    public LoadGameFilesUseCase(
            List<IGameCenterData> gamesData,
            FilesDownloadManager filesDownloadManager,
            DownloadInterruption interruption
    ) {
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
        final GetZipFileUseCase getZipFileUseCase =
                new GetZipFileUseCase(
                        archiveUrl,
                        data.archiveSize(),
                        data.archiveSha1()
                );
        final RemoveOldGameFilesUseCase removeOldGameFilesUseCase =
                new RemoveOldGameFilesUseCase(archiveUrl);
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
        gameUseCasesThread.submit(new Runnable() {
            @Override
            public void run() {
                final UseCaseCallback<String> unzipCallback = new SuccessUseCaseCallback<String>() {
                    @Override
                    public void onSuccess(final String result) {
                        if (interruption.active) return;
                        final String resourcesHash =
                                ProfilingManager.getInstance().addTask(
                                        "game_resources_download"
                                );
                        if (data.resources() == null || data.resources().isEmpty()) {
                            ProfilingManager.getInstance().setReady(resourcesHash);
                            if (successUseCaseCallback != null)
                                successUseCaseCallback.onSuccess(data);
                        } else {
                            DownloadResourcesUseCase downloadResourcesUseCase =
                                    new DownloadResourcesUseCase(
                                            filesDownloadManager,
                                            data.resources(),
                                            data.id(),
                                            archiveUrl,
                                            interruption,
                                            null,
                                            new SuccessUseCaseCallback<Void>() {
                                                @Override
                                                public void onSuccess(Void ignore) {
                                                    ProfilingManager.getInstance().setReady(resourcesHash);
                                                    if (successUseCaseCallback != null)
                                                        successUseCaseCallback.onSuccess(data);
                                                }
                                            }
                                    );
                            downloadResourcesUseCase.download();
                        }
                    }
                };
                removeOldGameFilesUseCase.remove();
                getZipFileUseCase.get(
                        interruption,
                        new SuccessUseCaseCallback<File>() {
                            @Override
                            public void onSuccess(File result) {
                                if (interruption.active) return;
                                File directory = new File(
                                        result.getParent() +
                                                File.separator +
                                                archiveUrl.hashCode());
                                final UnzipUseCase unzipUseCase =
                                        new UnzipUseCase(result.getAbsolutePath());
                                if (!directory.exists()) {
                                    boolean unzipResult = unzipUseCase.unzip(
                                            directory.getAbsolutePath(),
                                            null
                                    );
                                    if (!unzipResult) {
                                        unzipCallback.onError("Can't unarchive game");
                                        return;
                                    }
                                }
                                unzipCallback.onSuccess(directory.getAbsolutePath());
                            }
                        },
                        null,
                        finalTotalFilesSize
                );
            }
        });
    }
}
