package com.inappstory.sdk.core.utils.lrudiskcache;

import com.inappstory.sdk.core.cache.DownloadFileState;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

    public static final long MB_2000 = 2000 * MB_1;

    public static void clear() throws IOException {
        if (fastCache != null) fastCache.clearCache();
        if (commonCache != null) commonCache.clearCache();
        if (infiniteCache != null) infiniteCache.clearCache();
        fastCache = null;
        commonCache = null;
        infiniteCache = null;
    }

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

    private LruDiskCache(File cacheDir, String subPath, long cacheSize, CacheType cacheType) throws IOException {
        this.manager = new FileManager(cacheDir, subPath);
        this.journal = new CacheJournal(manager);
        this.cacheSize = cacheSize;
        this.cacheType = cacheType;
    }

    public File put(String key, File file) throws IOException {
        synchronized (journal) {
            keyIsValid(key);
            String name = file.getAbsolutePath();
            long time = System.currentTimeMillis();
            long fileSize = manager.getFileSize(file);
            CacheJournalItem item = new CacheJournalItem(key, name, time, fileSize);
            File cacheFile = manager.put(file, name);
            journal.delete(key, false);
            journal.put(item, cacheSize);
            journal.writeJournal();
            return cacheFile;
        }
    }


    public void clearUntilSize(long needSize) {
        synchronized (journal) {
            try {
                journal.checkFreeSize(needSize, getCacheSize());
            } catch (IOException ignored) {

            }
        }
    }

    public File put(String key, File file, long fileSize, long downloadedSize) throws IOException {
        synchronized (journal) {
            keyIsValid(key);
            String name = file.getAbsolutePath();
            long time = System.currentTimeMillis();
            CacheJournalItem item = new CacheJournalItem(key, name, time, fileSize, downloadedSize);
            File cacheFile = manager.put(file, name);
            journal.delete(key, false);
            journal.put(item, cacheSize);
            journal.writeJournal();
            return cacheFile;
        }
    }

    public void delete(String key) throws IOException {
        delete(key, true);
    }

    private void delete(String key, boolean writeJournal) throws IOException {
        synchronized (journal) {
            keyIsValid(key);
            CacheJournalItem item = journal.delete(key, true);
            if (item != null) {
                if (writeJournal) {
                    journal.writeJournal();
                }
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
    }

    public Set<String> keySet() {
        synchronized (journal) {
            return journal.keySet();
        }
    }

    public long getCacheSize() {
        synchronized (journal) {
            if (this == infiniteCache) {
                return getFreeSpace() -
                        (commonCache != null ? commonCache.getCacheSize() : 0) -
                        (fastCache != null ? fastCache.getCacheSize() : 0);
            }
            return cacheSize;
        }
    }

    public void setCacheSize(long cacheSize) {
        synchronized (journal) {
            this.cacheSize = cacheSize;
        }
    }

    public long getUsedSpace() {
        synchronized (journal) {
            return journal.getCurrentCacheSize();
        }
    }

    public long getFreeSpace() {
        synchronized (journal) {
            return cacheSize - journal.getCurrentCacheSize();
        }
    }

    public long getJournalSize() {
        synchronized (journal) {
            return journal.getJournalSize();
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
        return FileManager.getFullFile(get(key));
    }

    public DownloadFileState get(String key) {
        synchronized (journal) {
            try {
                keyIsValid(key);
                CacheJournalItem item = journal.get(key);
                if (item != null) {
                    File file = new File(item.getName());
                    if (!file.exists()) {
                        journal.delete(key, false);
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
