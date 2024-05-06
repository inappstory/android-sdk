package com.inappstory.sdk.game.cache;

import static com.inappstory.sdk.network.NetworkClient.NC_IS_UNAVAILABLE;

import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.GameLaunchConfigObject;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.ArchiveUseCase;
import com.inappstory.sdk.stories.cache.usecases.GameFolderUseCase;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
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
    private static final String INDEX_NAME = "index.html";
    public static final String FILE = "file://";
    private final ExecutorService gameUseCasesThread = Executors.newFixedThreadPool(1);

    public void getGame(
            final String gameId,
            final FilesDownloadManager filesDownloadManager,
            final DownloadInterruption interruption,
            final ProgressCallback progressCallback,
            final UseCaseWarnCallback<File> splashScreenCallback,
            final UseCaseCallback<GameCenterData> gameModelCallback,
            final UseCaseCallback<FilePathAndContent> gameLoadCallback,
            final SetGameLoggerCallback setGameLoggerCallback
    ) {
        final String[] oldSplashPath = {null};
        GetLocalSplashUseCase getLocalSplashUseCase = new GetLocalSplashUseCase(gameId);
        getLocalSplashUseCase.get(new UseCaseCallback<File>() {
            @Override
            public void onError(String message) {
                splashScreenCallback.onWarn(message);
            }

            @Override
            public void onSuccess(File result) {
                oldSplashPath[0] = result.getAbsolutePath();
                splashScreenCallback.onSuccess(result);
            }
        });
        new GetGameModelUseCase().get(gameId, new GameLoadCallback() {
            @Override
            public void onSuccess(GameCenterData data) {
                gameModelCallback.onSuccess(data);
                final String archiveUrl = data.url;

                DownloadSplashUseCase downloadSplashUseCase = new DownloadSplashUseCase(
                        filesDownloadManager,
                        data.splashScreen,
                        oldSplashPath[0],
                        gameId
                );
                downloadSplashUseCase.download(new UseCaseCallback<File>() {
                    @Override
                    public void onError(String message) {
                        splashScreenCallback.onWarn(message);
                    }

                    @Override
                    public void onSuccess(File result) {
                        KeyValueStorage.saveString(
                                "gameInstanceSplash_" + gameId,
                                result.getAbsolutePath()
                        );
                        if (oldSplashPath[0] == null) {
                            splashScreenCallback.onSuccess(result);
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
                                        String fileName = result + File.separator + INDEX_NAME;
                                        loadedFilePath = fileName;
                                        try {
                                            gameLoadCallback.onSuccess(
                                                    new FilePathAndContent(
                                                            FILE + fileName,
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
                    FILE + loadedFilePath,
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
