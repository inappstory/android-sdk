package com.inappstory.sdk.lrudiskcache;

import com.inappstory.sdk.stories.cache.DownloadFileState;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import kotlin.NotImplementedError;

public class LruDiskCache {

    private final CacheJournal journal;
    private FileManager manager;
    private long cacheSize;

    public CacheType cacheType;

    private static LruDiskCache fastCache;
    private static LruDiskCache commonCache;
    private static LruDiskCache infiniteCache;

    public static final long MB_1 = 1024 * 1024;
    public static final long MB_5 = 5 * MB_1;
    public static final long MB_10 = 10 * MB_1;
    public static final long MB_50 = 50 * MB_1;
    public static final long MB_100 = 100 * MB_1;
    public static final long MB_200 = 200 * MB_1;

    public File getCacheDir() {
        return manager.getCacheDir();
    }

    private static Object cacheLock = new Object();

    public static LruDiskCache create(File cacheDir, String subPath, long cacheSize, CacheType cacheType) throws IOException {
        synchronized (cacheLock) {
            if (cacheSize < MB_1)
                cacheSize = MB_1;
            switch (cacheType) {
                case COMMON:
                    if (commonCache == null)
                        commonCache = new LruDiskCache(cacheDir, subPath + "commonCache", cacheSize, cacheType);
                    return commonCache;
                case FAST:
                    if (fastCache == null)
                        fastCache = new LruDiskCache(cacheDir, subPath + "fastCache", cacheSize, cacheType);
                    return fastCache;
                case INFINITE:
                    if (infiniteCache == null)
                        infiniteCache = new LruDiskCache(cacheDir, subPath + "infiniteCache", cacheSize, cacheType);
                    return infiniteCache;
                default:
                    return null;
            }
        }
    }

    LruDiskCache(
            File cacheDir,
            String subPath,
            long cacheSize,
            CacheType cacheType
    ) throws IOException {
        this.manager = new FileManager(cacheDir, subPath);
        this.journal = new CacheJournal(manager);
        this.cacheSize = cacheSize;
        this.cacheType = cacheType;
    }

    public void put(String key, File file) throws IOException {
        throw new NoSuchMethodError();
        /*long fileSize = manager.getFileSize(file);
        put(key, file, fileSize, fileSize);*/
    }

    public void put(String key, File file, long fileSize, long downloadedSize) throws IOException {
        throw new NoSuchMethodError();
   /*     synchronized (journal) {
            keyIsValid(key);
            String name = file.getAbsolutePath();
            long time = System.currentTimeMillis();
            CacheJournalItem item = new CacheJournalItem(key, name, time, fileSize, downloadedSize);
            manager.put(file, name);
            journal.delete(key, false);
            journal.put(item, cacheSize);
            journal.writeJournal();
        }*/
    }

    public void put(CacheJournalItem item) throws IOException {
        synchronized (journal) {
            File file = new File(item.getFilePath());
            keyIsValid(item.getUniqueKey());
            String name = file.getAbsolutePath();
            manager.put(file, name);
            journal.delete(item.getUniqueKey(), false);
            journal.put(item, cacheSize);
            journal.writeJournal();
        }
    }

    public void delete(String key) throws IOException {
        delete(key, true);
    }

    private void delete(String key, boolean writeJournal) throws IOException {
        synchronized (journal) {
            keyIsValid(key);
            if (journal.delete(key, true) && writeJournal) {
                journal.writeJournal();
            }
        }
    }

    public void clearCache() throws IOException {
        synchronized (journal) {
            Set<String> keys = new HashSet<>(journal.keySet());
            for (String key : keys) {
                delete(key, false);
            }
            journal.writeJournal();
        }
        FileManager.deleteFolderRecursive(getCacheDir(), false);
    }

    public long getCacheSize() {
        synchronized (journal) {
            return cacheSize;
        }
    }

    public void setCacheSize(long cacheSize) {
        synchronized (journal) {
            this.cacheSize = cacheSize;
        }
    }

    public String getNameFromKey(String key) {
        return Utils.hash(key);
    }

    public File getFileFromKey(String key) {
        return new File(getCacheDir().getAbsolutePath() + File.separator + getNameFromKey(key));
    }

    public boolean hasKey(String key) {
        synchronized (journal) {
            keyIsValid(key);
            CacheJournalItem item = journal.get(key);
            return item != null;
        }
    }

    public File getFullFile(String key) {
        return getFullFile(key, null);
    }


    public File getFullFile(String key, String type) {
        return FileManager.getFullFile(get(key, type));
    }

    public DownloadFileState get(String key) {
        return get(key, null);
    }

    public CacheJournalItem getJournalItem(String key, String type) {
        return journal.get(key, type);
    }


    public CacheJournalItem getJournalItem(String key) {
        return getJournalItem(key, null);
    }

    public DownloadFileState get(String key, String type) {
        synchronized (journal) {
            try {
                keyIsValid(key);
                CacheJournalItem item = journal.get(key, type);
                if (item != null) {
                    File file = new File(item.getFilePath());
                    if (!file.exists()) {
                        journal.delete(key, type, false);
                        file = null;
                    }
                    journal.writeJournal();
                    if (file != null)
                        return new DownloadFileState(file, item.getSize(), item.getDownloadedSize());
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }
    }

    private void keyIsValid(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException(String.format("Invalid key value: '%s'", key));
        }
    }
}
