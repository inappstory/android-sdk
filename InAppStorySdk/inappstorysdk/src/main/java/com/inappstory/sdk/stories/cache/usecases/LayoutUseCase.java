package com.inappstory.sdk.stories.cache.usecases;


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
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

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
        } catch (IOException e) {

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
                        NetworkCallback<LayoutResponse> layoutCallback = new NetworkCallback<LayoutResponse>() {
                            @Override
                            public void onSuccess(LayoutResponse response) {
                                writeToFile(response.layout);
                                useCaseCallback.onSuccess(response.layout);
                            }

                            @Override
                            public Type getType() {
                                return LayoutResponse.class;
                            }

                            @Override
                            public void errorDefault(String message) {
                                useCaseCallback.onError(new SimpleUseCaseError(""));
                            }
                        };
                        core.network().enqueue(
                                core.network().getApi().getLayout(
                                        sessionData.userId,
                                        sessionData.sessionId,
                                        sessionData.locale
                                ),
                                layoutCallback
                        );
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

    private void writeToFile(String layout) {
        File file = new File(filePath);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream stream = new FileOutputStream(filePath)) {
            stream.write(layout.getBytes());
            getCache().put(generateCacheItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFile(File file) {
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try (FileInputStream stream = new FileInputStream(filePath)) {
            stream.read(bytes);
        } catch (Exception ignored) {
            return null;
        }
        return new String(bytes);
    }

    @Override
    protected CacheJournalItem generateCacheItem() {
        return new CacheJournalItem(
                uniqueKey,
                filePath,
                null,
                "ias_layout",
                null,
                null,
                System.currentTimeMillis(),
                0,
                0,
                null
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return core.contentLoader().getInfiniteCache();
    }
}
