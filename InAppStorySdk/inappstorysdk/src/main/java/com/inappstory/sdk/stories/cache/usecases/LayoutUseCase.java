package com.inappstory.sdk.stories.cache.usecases;


import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.network.content.models.LayoutResponse;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.parse.to.ToLayoutResponse;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LayoutUseCase extends GetCacheFileUseCase<Void> {
    private final UseCaseCallback<String> useCaseCallback;
    private final String timestamp;

    public LayoutUseCase(
            IASCore core,
            UseCaseCallback<String> useCaseCallback,
            String timestamp
    ) {
        super(core);
        this.useCaseCallback = useCaseCallback;
        this.uniqueKey = StringsUtils.md5(
                "ias_content_layout_" + core.projectSettingsAPI().apiKey()
        );
        this.timestamp = timestamp;
        this.filePath = new FilePathCacheGenerator(
                timestamp,
                core,
                FilePathCacheType.LAYOUT
        ).generate();
    }

    private void deleteCacheKey() {
        try {
            getCache().delete(uniqueKey);
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to delete layout cache key. Error: " + e.getMessage()
            );
        }
    }

    private void downloadFile() {

        new ConnectionCheck().check(
                core.appContext(),
                new ConnectionCheckCallback(core) {
                    @Override
                    public void success() {
                        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
                        CachedSessionData sessionData = settingsHolder.sessionData();
                        if (sessionData == null || sessionData.userId == null) {
                            InAppStoryManager.showDLog(
                                    LoggerTags.IAS_WARN_TAG,
                                    "Can't download layout. Session is not opened");
                            useCaseCallback.onError(new SimpleUseCaseError("Session is not opened"));
                            return;
                        }
                        NetworkCallback<LayoutResponse> layoutCallback =
                                new NetworkCallback<LayoutResponse>() {
                                    @Override
                                    public void onSuccess(LayoutResponse response) {
                                        if (response != null && response.layout != null) {
                                            if (writeToFile(response.layout)) {
                                                useCaseCallback.onSuccess(response.layout);
                                            } else {
                                                useCaseCallback.onError(new SimpleUseCaseError("Failed to save layout to disk"));
                                            }
                                        } else {
                                            useCaseCallback.onError(new SimpleUseCaseError("Empty layout response"));
                                        }
                                    }

                                    @Override
                                    public void errorDefault(String message) {
                                        String errorMessage = (message != null && !message.isEmpty()) ? message : "Network error";
                                        useCaseCallback.onError(new SimpleUseCaseError(errorMessage));
                                    }
                                };
                        core.network().enqueue(
                                core.network().getApi().getLayout(
                                        sessionData.userId,
                                        sessionData.sessionId,
                                        sessionData.locale
                                ),
                                layoutCallback,
                                ToLayoutResponse.class
                        );
                    }

                    @Override
                    public void error() {
                        InAppStoryManager.showDLog(
                                LoggerTags.IAS_WARN_TAG,
                                "Can't download layout. No internet connection"
                        );
                        useCaseCallback.onError(new SimpleUseCaseError("No internet connection"));
                    }
                }
        );
    }

    @Override
    public Void getFile() {
        if (!getLocalFile())
            downloadFile();
        return null;
    }

    @Override
    protected String journalItemType() {
        return "ias_layout";
    }


    public boolean getLocalFile() {
        CacheJournalItem cached = getCache().getJournalItem(uniqueKey);
        DownloadFileState fileState = null;

        if (cached != null) {
            fileState = getCache().get(uniqueKey);
            if (fileState != null) {
                File file = fileState.getFullFile();
                if (file != null &&
                        file.exists() &&
                        file.getAbsolutePath().equals(filePath)
                ) {
                    useCaseCallback.onSuccess(readFile(file));
                    return true;
                }
            }
        }

        deleteCacheKey();
        return false;
    }

    private boolean writeToFile(String layout) {
        File file = new File(filePath);
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(layout.getBytes());
                getCache().put(generateCacheItem(layout.length(), layout.length()));
                return true;
            }
        } catch (IOException e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Failed to write layout file. Error: " + e.getMessage()
            );
            return false;
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Failed to update cache for layout. Error: " + e.getMessage()
            );
            return false;
        }
    }

    private String readFile(File file) {
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Layout file is too large to read into memory"
            );
            return null;
        }

        byte[] bytes = new byte[(int) length];
        try (FileInputStream stream = new FileInputStream(file)) {
            int bytesRead = stream.read(bytes);
            if (bytesRead != length) {
                InAppStoryManager.showDLog(
                        LoggerTags.IAS_WARN_TAG,
                        "Could not read entire layout file. Expected: " + length + ", Read: " + bytesRead
                );
            }
        } catch (IOException e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Failed to read layout file. Error: " + e.getMessage()
            );
            return null;
        }
        return new String(bytes);
    }

    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getInfiniteCache();
    }
}
