package com.inappstory.sdk.lrudiskcache;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LruDiskCache {

    private final CacheJournal journal;
    private FileManager manager;
    private long cacheSize;

    private static LruDiskCache fastCache;
    private static LruDiskCache commonCache;

    public static final long MB_1 = 1024 * 1024;
    public static final long MB_5 = 5 * MB_1;
    public static final long MB_10 = 10 * MB_1;
    public static final long MB_50 = 50 * MB_1;
    public static final long MB_100 = 100 * MB_1;
    public static final long MB_200 = 200 * MB_1;

    public File getCacheDir() {
        return manager.getCacheDir();
    }

    public static LruDiskCache create(File cacheDir, long cacheSize, boolean isFastCache) throws IOException {
        if (cacheSize < MB_1)
            cacheSize = MB_1;
        if (isFastCache) {
            if (fastCache == null)
                fastCache = new LruDiskCache(cacheDir, cacheSize);
            return fastCache;
        } else {
            if (commonCache == null)
                commonCache = new LruDiskCache(cacheDir, cacheSize);
            return commonCache;
        }
    }

    private LruDiskCache(File cacheDir, long cacheSize) throws IOException {
        this.manager = new FileManager(cacheDir);
        this.journal = new CacheJournal(manager);
        this.cacheSize = cacheSize;
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

    public File get(String key) throws IOException {
        synchronized (journal) {
            keyIsValid(key);
            CacheJournalItem item = journal.get(key);
            if (item != null) {
                File file = new File(item.getName());
                if (!file.exists()) {
                    journal.delete(key, false);
                    file = null;
                }
                journal.writeJournal();
                return file;
            } else {
                return null;
            }
        }
    }

    private void keyIsValid(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException(String.format("Invalid key value: '%s'", key));
        }
    }
}
