package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.GameNameHolder;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UnzipUseCase;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.GameArchiveItem;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GameFolderUseCase extends GetCacheFileUseCase<Void> {
    private final String url;
    private final String archiveName;
    private final List<GameArchiveItem> expectedArchiveItems;
    private final UseCaseCallback<String> useCaseCallback;
    private final ProgressCallback progressCallback;
    private final String type = "Folder";
    private final FileChecker fileChecker;

    public GameFolderUseCase(
            IASCore core,
            String url,
            String gameName,
            FileChecker fileChecker,
            List<GameArchiveItem> expectedArchiveItems,
            UseCaseCallback<String> useCaseCallback,
            ProgressCallback progressCallback
    ) {
        super(core);
        this.url = url;
        this.fileChecker = fileChecker;
        this.useCaseCallback = useCaseCallback;
        this.progressCallback = progressCallback;
        this.expectedArchiveItems = expectedArchiveItems;
        this.archiveName = gameName;
        this.uniqueKey = StringsUtils.md5(url);

        this.filePath = new FilePathCacheGenerator(
                gameName,
                this.uniqueKey,
                core,
                FilePathCacheType.GAME_FOLDER
        ).generate();
    }

    @Override
    public Void getFile() {
        if (!checkIfAlreadyUnzipped())
            tryUnzipAndValidate();
        return null;
    }

    private boolean checkIfAlreadyUnzipped() {
        DownloadFileState fileState = getCache().get(uniqueKey, type);
        if (fileState != null) {
            return checkDirectory(fileState.file);
        } else {
            File directory = new File(this.filePath);
            if (checkDirectory(directory)) {
                putToCache(getFileSize(directory));
                return true;
            }
        }
        return false;
    }

    private void tryUnzipAndValidate() {
        String zipFilePath = this.filePath +
                StringsUtils.getExtensionFromUrl(url);
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            useCaseCallback.onError(new SimpleUseCaseError("Can't find archive"));
            return;
        }
        final UnzipUseCase unzipUseCase =
                new UnzipUseCase(zipFile.getAbsolutePath());
        boolean unzipResult = unzipUseCase.unzip(
                this.filePath,
                progressCallback
        );
        if (!unzipResult) {
            useCaseCallback.onError(new SimpleUseCaseError("Failed to unarchive game"));
        } else {
            File directory = new File(this.filePath);
            String checkRes = checkForExpectedItems(directory);
            if (checkRes == null) {
                progressCallback.onProgress(100, 100);
                putToCache(getFileSize(directory));
                useCaseCallback.onSuccess(this.filePath);
            } else {
                useCaseCallback.onError(new SimpleUseCaseError("Archive has wrong file structure. " + checkRes));
            }
        }
    }


    private boolean checkDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            if (checkForExpectedItems(directory) == null) {
                progressCallback.onProgress(100, 100);
                useCaseCallback.onSuccess(directory.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    private String checkForExpectedItems(File directory) {
        for (GameArchiveItem item : expectedArchiveItems) {
            if (item.sha1() == null)
                continue;
            File checkedFile = new File(directory, item.item());
            if (!checkedFile.exists()) {
                return "File " + item.item() + " is missing from archive";
            }
            String sha1 = fileChecker.getFileSHA1(checkedFile);
            if (!Objects.equals(sha1, item.sha1())) {
                return "File "
                        + item.item()
                        + " has sha1: "
                        + sha1
                        + ". Expected sha1: "
                        + item.sha1();
            }
        }
        return null;
    }

    private void putToCache(long size) {
        try {
            CacheJournalItem cacheJournalItem = generateCacheItem(size, size);
            getCache().put(cacheJournalItem, type);
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to update cache journal for game folder. Error: " + e.getMessage()
            );
        }
    }



    private long getFileSize(final File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return file != null ? file.length() : 0;
        }
        final List<File> dirs = new LinkedList<>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null)
                continue;
            for (final File child : listFiles) {
                if (child.isDirectory()) {
                    dirs.add(child);
                } else {
                    result += child.length();
                }
            }
        }
        return result;
    }

    @Override
    protected String journalItemType() {
        return type;
    }


    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getInfiniteCache();
    }
}
