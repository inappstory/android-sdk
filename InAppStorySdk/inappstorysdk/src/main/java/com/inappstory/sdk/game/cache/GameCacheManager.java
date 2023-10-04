package com.inappstory.sdk.game.cache;

import static com.inappstory.sdk.network.NetworkClient.NC_IS_UNAVAILABLE;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
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

    public void getGame(String gameId, GameLoadCallback callback) {
        getGameFromGameCenter(gameId, callback);
    }

    public GameCenterData getCachedGame(String gameId) {
        CachedGame game = cachedGames.get(gameId);
        if (game != null) {
            return game.data;
        }
        return null;
    }

    private static final String INDEX_NAME = "index.html";
    public static final String FILE = "file://";
    private final ExecutorService gameUseCasesThread = Executors.newFixedThreadPool(1);

    public void getGame(
            final String gameId,
            final DownloadInterruption interruption,
            final ProgressCallback progressCallback,
            final UseCaseCallback<File> splashScreenCallback,
            final UseCaseCallback<GameCenterData> gameModelCallback,
            final UseCaseCallback<FilePathAndContent> gameLoadCallback
    ) {
        final String[] oldSplashPath = {null};
        GetLocalSplashUseCase getLocalSplashUseCase = new GetLocalSplashUseCase(gameId);
        getLocalSplashUseCase.get(new UseCaseCallback<File>() {
            @Override
            public void onError(String message) {
                splashScreenCallback.onError(message);
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
                final GetZipFileUseCase getZipFileUseCase =
                        new GetZipFileUseCase(
                                archiveUrl,
                                data.archiveSize,
                                data.archiveSha1
                        );
                final DownloadResourcesUseCase downloadResourcesUseCase =
                        new DownloadResourcesUseCase(data.resources);
                final RemoveOldGameFilesUseCase removeOldGameFilesUseCase =
                        new RemoveOldGameFilesUseCase(archiveUrl);
                DownloadSplashUseCase downloadSplashUseCase = new DownloadSplashUseCase(
                        data.splashScreen,
                        oldSplashPath[0],
                        gameId
                );
                downloadSplashUseCase.download(new UseCaseCallback<File>() {
                    @Override
                    public void onError(String message) {
                        splashScreenCallback.onError(message);
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
                if (data.archiveUncompressedSize != null)
                    finalTotalFilesSize = totalArchiveSize + totalResourcesSize + data.archiveUncompressedSize;
                else
                    finalTotalFilesSize = totalArchiveSize + totalResourcesSize;
                final long finalTotalDownloadsSize = totalArchiveSize + totalResourcesSize;
                gameUseCasesThread.submit(new Runnable() {
                    @Override
                    public void run() {
                        final long[] totalProgress = {0};
                        final UseCaseCallback<String> unzipCallback = new UseCaseCallback<String>() {
                            @Override
                            public void onError(String message) {
                                gameLoadCallback.onError(message);
                            }

                            @Override
                            public void onSuccess(final String result) {
                                final String resourcesHash =
                                        ProfilingManager.getInstance().addTask(
                                                "game_resources_download"
                                        );
                                totalProgress[0] += 0.2 * finalTotalDownloadsSize;
                                String resourcesPath = result
                                        + File.separator
                                        + "resources_"
                                        + gameId
                                        + File.separator;
                                downloadResourcesUseCase.download(
                                        resourcesPath,
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
                                            public void onSuccess(Void ignore) {
                                                ProfilingManager.getInstance().setReady(resourcesHash);
                                                String fileName = result + File.separator + INDEX_NAME;
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
                            }
                        };
                        removeOldGameFilesUseCase.remove();
                        getZipFileUseCase.get(
                                interruption,
                                new UseCaseCallback<File>() {
                                    @Override
                                    public void onError(String message) {
                                        gameLoadCallback.onError(message);
                                    }

                                    @Override
                                    public void onSuccess(File result) {
                                        totalProgress[0] += result.length();
                                        File directory = new File(
                                                result.getParent() +
                                                        File.separator +
                                                        archiveUrl.hashCode());
                                        final UnzipUseCase unzipUseCase =
                                                new UnzipUseCase(result.getAbsolutePath());
                                        if (!directory.exists()) {
                                            boolean unzipResult = unzipUseCase.unzip(
                                                    directory.getAbsolutePath(),
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
                                                    }
                                            );
                                            if (!unzipResult) {
                                                unzipCallback.onError("Can't unarchive game");
                                                return;
                                            }
                                        }
                                        unzipCallback.onSuccess(directory.getAbsolutePath());
                                    }
                                },
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
                                finalTotalFilesSize
                        );
                    }
                });
            }

            @Override
            public void onError(String message) {
                gameLoadCallback.onError("Can't retrieve game from game center");
            }
        });
    }

    private void getGameFromGameCenter(final String gameId, final GameLoadCallback callback) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            callback.onError(NC_IS_UNAVAILABLE);
            return;
        }
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                networkClient.enqueue(
                        networkClient.getApi().getGameByInstanceId(gameId),
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
    }
}
