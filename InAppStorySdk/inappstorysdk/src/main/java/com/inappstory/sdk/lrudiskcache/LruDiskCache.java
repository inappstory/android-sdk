package com.inappstory.sdk.lrudiskcache;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.cache.DownloadFileState;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LruDiskCache {

    private final CacheJournal journal;
    private final FileManager manager;
    private long cacheSize;

    public CacheType cacheType;

    public static final long MB_1 = 1024 * 1024;
    public static final long MB_5 = 5 * MB_1;
    public static final long MB_10 = 10 * MB_1;
    public static final long MB_50 = 50 * MB_1;
    public static final long MB_100 = 100 * MB_1;
    public static final long MB_200 = 200 * MB_1;
    public static final long MB_500 = 500 * MB_1;

    public File getCacheDir() {
        return manager.getCacheDir();
    }

    LruDiskCache(
            IASCore core,
            File cacheDir,
            String subPath,
            long cacheSize,
            CacheType cacheType
    ) throws IOException {
        this.manager = new FileManager(core, cacheDir, subPath);
        this.journal = new CacheJournal(core, manager);
        this.cacheSize = cacheSize;
        this.cacheType = cacheType;
    }


    public void put(CacheJournalItem item) throws IOException {
        put(item, null);
    }

    public void put(CacheJournalItem item, String type) throws IOException {
        synchronized (journal) {
            File file = new File(item.getFilePath());
            keyIsValid(item.getUniqueKey());
            String name = file.getAbsolutePath();
            manager.put(file, name);
            journal.delete(item.getUniqueKey(), type, false);
            journal.put(item, cacheSize);
            journal.writeJournal();
        }
    }

    public void delete(CacheJournalItem item, String type) throws IOException {
        synchronized (journal) {
            File file = new File(item.getFilePath());
            keyIsValid(item.getUniqueKey());
            String name = file.getAbsolutePath();
            manager.delete(name);
            journal.delete(item.getUniqueKey(), type, false);
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
