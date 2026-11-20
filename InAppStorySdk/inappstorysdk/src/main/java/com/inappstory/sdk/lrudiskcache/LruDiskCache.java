package com.inappstory.sdk.lrudiskcache;

import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class LruDiskCache {

    private CacheJournal journal;
    private FileManager manager;
    private long cacheSize = MB_1;

    private final Object lock = new Object();

    public CacheType cacheType = CacheType.INFINITE;

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
            File cacheDir,
            String subPath,
            long cacheSize,
            CacheType cacheType,
            ExceptionManager exceptionManager
    ) {
        try {
            this.manager = new FileManager(cacheDir, subPath);
            this.journal = new CacheJournal(manager, exceptionManager);
            this.cacheSize = cacheSize;
            this.cacheType = cacheType;
        } catch (Exception e) {
        }
    }


    public void put(CacheJournalItem item) throws Exception {
        put(item, null);
    }

    public void put(CacheJournalItem item, String type) throws Exception {
        synchronized (lock) {
            File file = new File(item.getFilePath());
            keyIsValid(item.getUniqueKey());
            String name = file.getAbsolutePath();
            manager.put(file, name);
            journal.delete(item.getUniqueKey(), type, false);
            journal.put(item, cacheSize);
            journal.scheduleWriteJournal();
        }
    }

    public void delete(CacheJournalItem item, String type) throws Exception {
        synchronized (lock) {
            File file = new File(item.getFilePath());
            keyIsValid(item.getUniqueKey());
            String name = file.getAbsolutePath();
            manager.delete(name);
            journal.delete(item.getUniqueKey(), type, false);
            journal.scheduleWriteJournal();
        }
    }

    public void delete(String key) throws Exception {
        delete(key, true);
    }

    private void delete(String key, boolean writeJournal) throws Exception {
        synchronized (lock) {
            keyIsValid(key);
            if (journal.delete(key, true) && writeJournal) {
                journal.scheduleWriteJournal();
            }
        }
    }

    public void clearCache() throws Exception {
        synchronized (lock) {
            Set<String> keys = new HashSet<>(journal.keySet());
            for (String key : keys) {
                delete(key, false);
            }
            journal.forceWriteJournal();
        }
        FileManager.deleteFolderRecursive(getCacheDir(), false, CacheJournal.JOURNAL_NAME);

    }

    public long getCacheSize() {
        synchronized (lock) {
            return cacheSize;
        }
    }

    public void setCacheSize(long cacheSize) {
        synchronized (lock) {
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
        synchronized (lock) {
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
        synchronized (lock) {
            try {
                keyIsValid(key);
                CacheJournalItem item = journal.get(key, type);
                if (item != null) {
                    File file = new File(item.getFilePath());
                    if (!file.exists()) {
                        journal.delete(key, type, false);
                        file = null;
                    }
                    journal.scheduleWriteJournal();
                    if (file != null)
                        return new DownloadFileState(file, item.getSize(), item.getDownloadedSize());
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }
    }

    public void deleteArchiveAndExtractedFolder(String key, String type) throws Exception {
        synchronized (lock) {
            journal.delete(key, type, true);
            journal.scheduleWriteJournal();
            File expectedFile = getFileFromKey(key);
            File extractDir = new File(expectedFile.getParentFile(), key);

            if (extractDir.exists() && extractDir.isDirectory()) {
                FileManager.deleteFolderRecursive(extractDir, true, null);
            }
        }
    }

    public void clearOldArchiveVersions(String archiveName, boolean inclusive) {
        File cacheDir = getCacheDir();
        File archiveDir = new File(new File(new File(cacheDir, "v2"), "zip"), archiveName);
        File expectedBase = new File(new File(cacheDir, "v2"), "zip");
        if (!archiveDir.getAbsolutePath().startsWith(expectedBase.getAbsolutePath())) {
            return;
        }

        if (archiveDir.exists() && archiveDir.isDirectory()) {
            FileManager.deleteFolderRecursive(archiveDir, inclusive, null);
        }
    }

    private void keyIsValid(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException(String.format("Invalid key value: '%s'", key));
        }
    }
}
