package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.UnzipUseCase;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class GameFolderUseCase extends GetCacheFileUseCase<Void> {
    private final String url;
    private final String archiveName;
    private final UseCaseCallback<String> useCaseCallback;
    private final ProgressCallback progressCallback;
    private final String type = "Folder";

    public GameFolderUseCase(
            IASCore core,
            String url,
            UseCaseCallback<String> useCaseCallback,
            ProgressCallback progressCallback
    ) {
        super(core);
        this.url = url;
        this.useCaseCallback = useCaseCallback;
        this.progressCallback = progressCallback;
        this.archiveName = getArchiveName(url);
        this.uniqueKey = StringsUtils.md5(url);
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "zip" +
                File.separator +
                archiveName +
                File.separator +
                uniqueKey;

    }

    private String getArchiveName(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        String[] nameParts = fName.split("_");
        if (nameParts.length > 0) return nameParts[0];
        return "";
    }

    private boolean checkDirectory(File directory) {
        if (directory.exists()) {
            progressCallback.onProgress(100, 100);
            useCaseCallback.onSuccess(directory.getAbsolutePath());
            return true;
        }
        return false;
    }

    private void putToCache(long size) {
        try {
            CacheJournalItem cacheJournalItem = generateCacheItem();
            cacheJournalItem.setSize(size);
            getCache().put(cacheJournalItem, type);
        } catch (Exception e) {

        }
    }

    @Override
    public Void getFile() {
        DownloadFileState fileState = getCache().get(uniqueKey, type);
        if (fileState != null) {
            if (checkDirectory(fileState.file)) return null;
        } else {
            File directory = new File(this.filePath);
            if (checkDirectory(directory)) {
                putToCache(getFileSize(directory));
                return null;
            }
        }
        String zipFilePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "zip" +
                File.separator +
                archiveName +
                File.separator +
                uniqueKey +
                FilesDownloader.getFileExtensionFromUrl(url);
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            useCaseCallback.onError("Can't find archive");
        }
        final UnzipUseCase unzipUseCase =
                new UnzipUseCase(zipFilePath);
        boolean unzipResult = unzipUseCase.unzip(
                filePath,
                progressCallback
        );
        if (!unzipResult) {
            useCaseCallback.onError("Can't unarchive game");
        } else {
            progressCallback.onProgress(100, 100);
            File directory = new File(this.filePath);
            putToCache(getFileSize(directory));
            useCaseCallback.onSuccess(filePath);
        }
        return null;
    }

    private long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
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
    protected CacheJournalItem generateCacheItem() {
        return new CacheJournalItem(
                uniqueKey,
                filePath,
                null,
                type,
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
