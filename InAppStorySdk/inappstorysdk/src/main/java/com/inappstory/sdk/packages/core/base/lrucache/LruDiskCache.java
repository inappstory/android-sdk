package com.inappstory.sdk.packages.core.base.lrucache;

import com.inappstory.sdk.packages.core.base.files.FileManager;
import com.inappstory.sdk.packages.core.base.lrucache.models.ILruCacheKey;
import com.inappstory.sdk.packages.core.base.lrucache.models.ILruDiskCacheJournalItem;
import com.inappstory.sdk.packages.core.base.lrucache.models.ILruDiskFileItem;
import com.inappstory.sdk.packages.core.base.lrucache.models.LruDiskCacheJournalItem;
import com.inappstory.sdk.packages.core.base.lrucache.models.LruDiskFileItem;

import java.io.File;
import java.io.IOException;


public class LruDiskCache implements ILruCacheWithRemove<ILruCacheKey, ILruDiskFileItem>, ISizeLimitedStorage {

    private final ILruCacheWithRemove<ILruCacheKey, ILruDiskCacheJournalItem> journal;
    private final String cacheDir;
    private long cacheSize;
    private long usedSize = 0;
    private final FileManager fileManager;


    public LruDiskCache(String cacheDir, FileManager fileManager) {
        this.cacheDir = cacheDir;
        this.fileManager = fileManager;
        String journalFilePath = cacheDir + File.separator + "journal.bin";
        OnSizeUpdate sizeUpdate = new OnSizeUpdate() {
            @Override
            public void update(long size) {
                usedSize = size;
            }
        };
        this.journal = new LruDiskCacheJournal(journalFilePath, sizeUpdate);
    }

    @Override
    public void put(ILruCacheKey key, ILruDiskFileItem item) {
        while (item.downloadedSize() + usedSize > cacheSize) {
            ILruDiskCacheJournalItem oldestItem = this.journal.getOldest();
            if (oldestItem != null) {
                this.journal.delete(oldestItem.key());
                try {
                    this.fileManager.deleteFile(oldestItem.filePath(), true);
                } catch (IOException ignored) {

                }
            }
        }
        this.journal.put(
                key,
                new LruDiskCacheJournalItem(
                        key,
                        item.filePath(),
                        item.sha1(),
                        item.downloadedSize(),
                        item.fullSize()
                )
        );
    }

    @Override
    public ILruDiskFileItem get(ILruCacheKey key) {
        ILruDiskCacheJournalItem journalItem = journal.get(key);
        if (journalItem != null) {
            return new LruDiskFileItem(
                    journalItem.filePath(),
                    journalItem.sha1(),
                    journalItem.downloadedSize(),
                    journalItem.fullSize()
            );
        }
        return null;
    }

    @Override
    public ILruDiskFileItem getOldest() {
        throw new NoSuchMethodError();
    }

    @Override
    public void clear() {
        this.journal.clear();
        try {
            this.fileManager.deleteFile(cacheDir, false);
        } catch (IOException ignored) {

        }
    }

    @Override
    public boolean delete(ILruCacheKey key) {
        ILruDiskCacheJournalItem item = this.journal.get(key);
        if (item != null) {
            this.journal.delete(item.key());
            try {
                this.fileManager.deleteFile(item.filePath(), true);
            } catch (IOException ignored) {

            }
            return true;
        }
        return false;
    }

    @Override
    public void size(long size) {
        this.cacheSize = size;
    }
}
