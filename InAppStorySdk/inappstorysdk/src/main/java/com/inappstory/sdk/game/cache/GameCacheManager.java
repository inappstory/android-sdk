package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.game.utils.GameConstants;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.ArchiveUseCase;
import com.inappstory.sdk.stories.cache.usecases.GameFolderUseCase;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
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

    /* public void getGame(String gameId, GameLoadCallback callback) {
         getGameFromGameCenter(gameId, callback);
     }

     public GameCenterData getCachedGame(String gameId) {
         CachedGame game = cachedGames.get(gameId);
         if (game != null) {
             return game.data;
         }
         return null;
     }
 */
    private final ExecutorService gameUseCasesThread = Executors.newFixedThreadPool(1);


    public void getGame(
            final String gameId,
            final boolean useAnimSplash,
            final FilesDownloadManager filesDownloadManager,
            final DownloadInterruption interruption,
            final ProgressCallback progressCallback,
            final UseCaseWarnCallback<Map<String, File>> splashScreenCallback,
            final UseCaseCallback<IGameCenterData> gameModelCallback,
            final UseCaseCallback<FilePathAndContent> gameLoadCallback,
            final SetGameLoggerCallback setGameLoggerCallback
    ) {
        final Map<String, File> localSplashFiles = new HashMap<>();

        final Map<String, String> splashesKeyValueStorageKeys = GameConstants.getSplashesKeys(useAnimSplash);
        final Map<String, File> splashFiles = new HashMap<>();
        final String animKey = GameConstants.SPLASH_ANIM;
        final String staticKey = GameConstants.SPLASH_STATIC;
        GetLocalSplashesUseCase getLocalSplashesUseCase = new GetLocalSplashesUseCase(
                gameId,
                splashesKeyValueStorageKeys
        );
        getLocalSplashesUseCase.get(new UseCaseCallback<Map<String, File>>() {
            @Override
            public void onError(String message) {
                splashScreenCallback.onWarn(message);
            }

            @Override
            public void onSuccess(Map<String, File> result) {
                localSplashFiles.clear();
                localSplashFiles.putAll(result);
                splashScreenCallback.onSuccess(localSplashFiles);
            }
        });
        new GetGameModelUseCase().get(gameId, new GameLoadCallback() {
            @Override
            public void onSuccess(GameCenterData data) {
                gameModelCallback.onSuccess(data);
                final String archiveUrl = data.url;
                final DownloadSplashUseCase downloadAnimSplashUseCase;
                if (useAnimSplash) {
                    String animFilePath = null;
                    File animFile = splashFiles.get(animKey);
                    if (animFile != null) {
                        animFilePath = animFile.getAbsolutePath();
                    }
                    downloadAnimSplashUseCase =  new DownloadSplashUseCase(
                            filesDownloadManager,
                            data.splashAnimation,
                            animFilePath,
                            splashesKeyValueStorageKeys.get(animKey),
                            gameId
                    );
                } else {
                    downloadAnimSplashUseCase = null;
                }
                String staticFilePath = null;
                File staticFile = splashFiles.get(staticKey);
                if (staticFile != null) {
                    staticFilePath = staticFile.getAbsolutePath();
                }

                DownloadSplashUseCase downloadSplashUseCase = new DownloadSplashUseCase(
                        filesDownloadManager,
                        data.splashScreen,
                        staticFilePath,
                        splashesKeyValueStorageKeys.get(staticKey),
                        gameId
                );
                final UseCaseCallback<File> animSplashDownloadCallback = new UseCaseCallback<File>() {
                    @Override
                    public void onError(String message) {
                        splashScreenCallback.onWarn(message);
                        if (localSplashFiles.isEmpty()) {
                            splashScreenCallback.onSuccess(splashFiles);
                        }
                    }

                    @Override
                    public void onSuccess(File result) {
                        KeyValueStorage.saveString(
                                splashesKeyValueStorageKeys.get(animKey) + gameId,
                                result.getAbsolutePath()
                        );
                        splashFiles.put(animKey, result);
                        if (localSplashFiles.isEmpty()) {
                            splashScreenCallback.onSuccess(splashFiles);
                        }
                    }
                };
                downloadSplashUseCase.download(new UseCaseCallback<File>() {
                    @Override
                    public void onError(String message) {
                        splashScreenCallback.onWarn(message);
                        if (downloadAnimSplashUseCase != null) {
                            downloadAnimSplashUseCase.download(animSplashDownloadCallback);
                        }
                    }

                    @Override
                    public void onSuccess(File result) {
                        KeyValueStorage.saveString(
                                splashesKeyValueStorageKeys.get(staticKey) + gameId,
                                result.getAbsolutePath()
                        );
                        splashFiles.put(staticKey, result);
                        if (downloadAnimSplashUseCase != null) {
                            downloadAnimSplashUseCase.download(animSplashDownloadCallback);
                        }

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
                                filesDownloadManager,
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
                                        ProfilingManager.getInstance().setReady(resourcesHash[0]);
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
                        filesDownloadManager,
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
                                resourcesHash[0] = ProfilingManager.getInstance().addTask(
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
                                filesDownloadManager,
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

  /*  private void getGameFromGameCenter(final String gameId, final GameLoadCallback callback) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            callback.onError(NC_IS_UNAVAILABLE);
            return;
        }

        final boolean demoMode;
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null)
            demoMode = inAppStoryManager.isGameDemoMode();
        else
            demoMode = false;
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(String sessionId) {
                networkClient.enqueue(
                        networkClient.getApi().getGameByInstanceId(
                                gameId, new GameLaunchConfigObject(demoMode)
                        ),
                        new NetworkCallback<GameCenterData>() {
                            @Override
                            public void onSuccess(final GameCenterData response) {
                                if (response.url == null ||
                                        response.url.isEmpty() ||
                                        response.initCode == null ||
                                        response.initCode.isEmpty()
                                ) {
                                    callback.onError("Invalid game data");
                                    return;
                                }
                                cachedGames.put(gameId, new CachedGame(response));
                                callback.onSuccess(response);
                            }

                            @Override
                            public Type getType() {
                                return GameCenterData.class;
                            }

                            @Override
                            public void errorDefault(String message) {
                                callback.onError(message);
                            }

                            @Override
                            public void timeoutError() {
                                callback.onError("Game loading run out of time");
                            }
                        }
                );
            }

            @Override
            public void onError() {
                callback.onError("Open session error");
            }
        });
    }*/
}
