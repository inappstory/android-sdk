package com.inappstory.sdk.game.cache;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.utils.GameConstants;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.ArchiveUseCase;
import com.inappstory.sdk.stories.cache.usecases.GameFolderUseCase;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameCacheManager {
    HashMap<String, CachedGame> cachedGames = new HashMap<>();

    public void clearGames() {
        cachedGames.clear();
    }

    private final IASCore core;


    public GameCacheManager(IASCore core) {
        this.core = core;
    }

    private final ExecutorService gameUseCasesThread = Executors.newFixedThreadPool(1);


    public void getGame(
            final String gameId,
            final boolean useAnimSplash,
            final FilesDownloadManager filesDownloadManager,
            final DownloadInterruption interruption,
            final ProgressCallback progressCallback,
            final UseCaseWarnCallback<File> staticSplashScreenCallback,
            final UseCaseWarnCallback<File> animSplashScreenCallback,
            final UseCaseCallback<IGameCenterData> gameModelCallback,
            final UseCaseCallback<FilePathAndContent> gameLoadCallback,
            final SetGameLoggerCallback setGameLoggerCallback
    ) {
        final Map<String, File> localSplashFiles = new HashMap<>();

        final Map<String, String> splashesKeyValueStorageKeys = GameConstants.getSplashesKeys(useAnimSplash);
        final String animKey = GameConstants.SPLASH_ANIM;
        final String staticKey = GameConstants.SPLASH_STATIC;
        final String animStorageKey = GameConstants.SPLASH_ANIM_KV;
        final String staticStorageKey = GameConstants.SPLASH_STATIC_KV;
        GetLocalSplashUseCase getLocalStaticSplashUseCase = new GetLocalSplashUseCase(
                core,
                gameId,
                staticStorageKey
        );
        GetLocalSplashUseCase getLocalAnimSplashUseCase = new GetLocalSplashUseCase(
                core,
                gameId,
                animStorageKey
        );
        getLocalStaticSplashUseCase.get(new UseCaseCallback<File>() {
            @Override
            public void onError(String message) {
                if (staticSplashScreenCallback != null)
                    staticSplashScreenCallback.onWarn(message);
            }

            @Override
            public void onSuccess(File result) {
                if (result == null) return;
                if (result.exists()) {
                    localSplashFiles.put(staticKey, result);

                    if (staticSplashScreenCallback != null)
                        staticSplashScreenCallback.onSuccess(result);
                }
            }
        });
        getLocalAnimSplashUseCase.get(new UseCaseCallback<File>() {
            @Override
            public void onError(String message) {
                if (animSplashScreenCallback != null)
                    animSplashScreenCallback.onWarn(message);
            }

            @Override
            public void onSuccess(File result) {
                if (result == null) return;
                if (result.exists()) {
                    localSplashFiles.put(animKey, result);

                    if (animSplashScreenCallback != null)
                        animSplashScreenCallback.onSuccess(result);
                } else {
                    Log.e("Game_Loading", result.getAbsolutePath() + " not exists");
                }
            }
        });
        new GetGameModelUseCase(core).get(gameId, new GameLoadCallback() {
            @Override
            public void onSuccess(GameCenterData data) {
                gameModelCallback.onSuccess(data);
                final String archiveUrl = data.url;
                final DownloadSplashUseCase downloadAnimSplashUseCase;
                if (useAnimSplash) {
                    String animFilePath = null;
                    File animFile = localSplashFiles.get(animKey);
                    if (animFile != null) {
                        animFilePath = animFile.getAbsolutePath();
                    }
                    downloadAnimSplashUseCase = new DownloadSplashUseCase(
                            core,
                            data.splashAnimation,
                            animFilePath,
                            animStorageKey,
                            gameId
                    );
                } else {
                    if (animSplashScreenCallback != null)
                        animSplashScreenCallback.onSuccess(null);
                    downloadAnimSplashUseCase = null;
                }
                String staticFilePath = null;
                File staticFile = localSplashFiles.get(staticKey);
                if (staticFile != null) {
                    staticFilePath = staticFile.getAbsolutePath();
                }

                DownloadSplashUseCase downloadSplashUseCase = new DownloadSplashUseCase(
                        core,
                        data.splashScreen,
                        staticFilePath,
                        staticStorageKey,
                        gameId
                );
                final UseCaseCallback<File> animSplashDownloadCallback = new UseCaseCallback<File>() {
                    @Override
                    public void onError(String message) {
                        if (animSplashScreenCallback != null)
                            animSplashScreenCallback.onSuccess(null);
                    }

                    @Override
                    public void onSuccess(File result) {
                        if (result == null) return;
                        if (result.exists()) {
                            core.keyValueStorage().saveString(
                                    splashesKeyValueStorageKeys.get(animKey) + gameId,
                                    result.getAbsolutePath()
                            );
                            if (localSplashFiles.get(animKey) == null)
                                if (animSplashScreenCallback != null)
                                    animSplashScreenCallback.onSuccess(result);
                        } else {
                            Log.e("Game_Loading", result.getAbsolutePath() + " not exists");
                            if (animSplashScreenCallback != null)
                                animSplashScreenCallback.onSuccess(null);
                        }
                    }
                };
                downloadSplashUseCase.download(new UseCaseCallback<File>() {
                    @Override
                    public void onError(String message) {
                        if (staticSplashScreenCallback != null)
                            staticSplashScreenCallback.onWarn(message);
                        if (downloadAnimSplashUseCase != null) {
                            downloadAnimSplashUseCase.download(animSplashDownloadCallback);
                        }
                    }

                    @Override
                    public void onSuccess(File result) {
                        if (result == null) return;
                        core.keyValueStorage().saveString(
                                splashesKeyValueStorageKeys.get(staticKey) + gameId,
                                result.getAbsolutePath()
                        );
                        if (downloadAnimSplashUseCase != null) {
                            downloadAnimSplashUseCase.download(animSplashDownloadCallback);
                        }
                        if (localSplashFiles.get(staticKey) == null)
                            if (staticSplashScreenCallback != null)
                                staticSplashScreenCallback.onSuccess(result);
                    }
                });
                final long totalArchiveSize;
                final long totalResourcesSize;
                long tempResourcesSize = 0;
                if (data.archiveSize != null)
                    totalArchiveSize = data.archiveSize;
                else
                    totalArchiveSize = 0;
                if (data.resources != null)
                    for (WebResource resource : data.resources) {
                        tempResourcesSize += resource.size;
                    }
                totalResourcesSize = tempResourcesSize;
                final long finalTotalFilesSize;
                final long finalTotalDownloadsSize = totalArchiveSize + totalResourcesSize;
                if (data.archiveUncompressedSize != null)
                    finalTotalFilesSize = finalTotalDownloadsSize + data.archiveUncompressedSize;
                else
                    finalTotalFilesSize = finalTotalDownloadsSize;

                final long[] totalProgress = {0};
                final String[] resourcesHash = {""};
                final String[] gameFolder = {""};
                final DownloadResourcesUseCase downloadResourcesUseCase =
                        new DownloadResourcesUseCase(
                                core,
                                data.resources,
                                gameId,
                                archiveUrl,
                                interruption,
                                new ProgressCallback() {
                                    @Override
                                    public void onProgress(long loadedSize, long totalSize) {
                                        long resultTotalSize;
                                        if (totalArchiveSize == 0)
                                            resultTotalSize = (long) (1.2f * totalSize);
                                        else
                                            resultTotalSize = (long) (1.2f * finalTotalDownloadsSize);
                                        progressCallback.onProgress(
                                                totalProgress[0] + loadedSize,
                                                resultTotalSize
                                        );
                                    }
                                },
                                new UseCaseCallback<Void>() {
                                    @Override
                                    public void onError(String message) {
                                        gameLoadCallback.onError(message);
                                    }

                                    @Override
                                    public void onSuccess(Void result) {
                                        progressCallback.onProgress(
                                                finalTotalFilesSize,
                                                finalTotalFilesSize
                                        );
                                        core.statistic().profiling().setReady(resourcesHash[0]);
                                        String fileName = gameFolder[0] + File.separator + GameConstants.INDEX_NAME;
                                        loadedFilePath = fileName;
                                        try {
                                            gameLoadCallback.onSuccess(
                                                    new FilePathAndContent(
                                                            GameConstants.FILE + fileName,
                                                            FileManager.getStringFromFile(new File(fileName))
                                                    )
                                            );
                                        } catch (Exception e) {
                                            gameLoadCallback.onError(e.getMessage());
                                        }
                                    }
                                }
                        );


                final GameFolderUseCase gameFolderUseCase = new GameFolderUseCase(
                        core,
                        archiveUrl,
                        new UseCaseCallback<String>() {
                            @Override
                            public void onError(String message) {
                                gameLoadCallback.onError(message);
                            }

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
                        new ProgressCallback() {
                            @Override
                            public void onProgress(long loadedSize, long totalSize) {
                                long resultTotalSize;
                                if (totalArchiveSize == 0)
                                    resultTotalSize = (long) (1.2f * (totalSize + totalResourcesSize));
                                else
                                    resultTotalSize = (long) (1.2f * finalTotalDownloadsSize);
                                progressCallback.onProgress(
                                        (long) (totalProgress[0] + (0.2f * loadedSize)),
                                        resultTotalSize
                                );
                            }
                        }
                );

                final ArchiveUseCase getZipFileUseCase =
                        new ArchiveUseCase(
                                core,
                                archiveUrl,
                                data.archiveSize,
                                data.archiveSha1,
                                finalTotalFilesSize,
                                new ProgressCallback() {
                                    @Override
                                    public void onProgress(long loadedSize, long totalSize) {
                                        long resultTotalSize;
                                        if (totalArchiveSize == 0)
                                            resultTotalSize = (long) (1.2f * totalSize);
                                        else
                                            resultTotalSize = (long) (1.2f * finalTotalDownloadsSize);
                                        progressCallback.onProgress(
                                                totalProgress[0] + loadedSize,
                                                resultTotalSize
                                        );
                                    }
                                },
                                interruption,
                                new UseCaseCallback<File>() {
                                    @Override
                                    public void onError(String message) {
                                        gameLoadCallback.onError(message);
                                    }

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

            @Override
            public void onError(String message) {
                Log.e("Game_Loading", message);
                gameLoadCallback.onError("Can't retrieve game from game center");
            }

            @Override
            public void onCreateLog(int loggerLevel) {
                setGameLoggerCallback.setLogger(loggerLevel);
            }
        });
    }

    private String loadedFilePath = "";

    public FilePathAndContent getCurrentFilePathAndContent() {
        if (loadedFilePath == null || loadedFilePath.isEmpty()) return null;
        try {
            return new FilePathAndContent(
                    GameConstants.FILE + loadedFilePath,
                    FileManager.getStringFromFile(new File(loadedFilePath))
            );
        } catch (Exception e) {
            return null;
        }
    }
}
