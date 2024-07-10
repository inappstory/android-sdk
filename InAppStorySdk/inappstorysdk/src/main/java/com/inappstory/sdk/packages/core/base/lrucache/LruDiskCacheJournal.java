package com.inappstory.sdk.packages.core.base.lrucache;

import androidx.annotation.NonNull;

import com.inappstory.sdk.packages.core.base.lrucache.models.ILruCacheKey;
import com.inappstory.sdk.packages.core.base.lrucache.models.ILruDiskCacheJournalItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LruDiskCacheJournal implements ILruCacheWithRemove<ILruCacheKey, ILruDiskCacheJournalItem> {
    private final String journalFilePath;
    private final Map<ILruCacheKey, ILruDiskCacheJournalItem> items;
    private final @NonNull OnSizeUpdate sizeUpdate;

    public LruDiskCacheJournal(String journalFilePath, @NonNull OnSizeUpdate sizeUpdate) {
        this.items = new HashMap<>();
        this.sizeUpdate = sizeUpdate;
        this.journalFilePath = journalFilePath;
        parseJournalFile();
    }

    private void parseJournalFile() {
    }

    private void saveJournalFile() {
    }

    private void saveEmptyJournalFile() {
    }

    private final Object lock = new Object();

    @Override
    public void put(ILruCacheKey key, ILruDiskCacheJournalItem item) {
        synchronized (lock) {
            this.items.put(key, item);
        }
        refreshSize();
        saveJournalFile();
    }

    private void refreshSize() {
        long totalSize = 0;
        List<ILruDiskCacheJournalItem> localItems;
        synchronized (lock) {
            localItems = new ArrayList<>(this.items.values());
        }
        for (ILruDiskCacheJournalItem localItem : localItems) {
            totalSize += localItem.downloadedSize();
        }
        this.sizeUpdate.update(totalSize);
    }

    @Override
    public ILruDiskCacheJournalItem get(ILruCacheKey key) {
        ILruDiskCacheJournalItem item = null;
        synchronized (lock) {
            item = this.items.get(key);
        }
        if (item != null) {
            item.updateLastUsedTime();
            saveJournalFile();
        }
        return item;
    }

    @Override
    public ILruDiskCacheJournalItem getOldest() {
        ILruDiskCacheJournalItem oldestItem = null;
        List<ILruDiskCacheJournalItem> localItems;
        synchronized (lock) {
            localItems = new ArrayList<>(this.items.values());
        }
        for (ILruDiskCacheJournalItem localItem : localItems) {
            if (oldestItem == null || oldestItem.lastUsedTime() > localItem.lastUsedTime()) {
                oldestItem = localItem;
            }
        }
        return oldestItem;
    }

    @Override
    public void clear() {
        boolean sizeNotNull;
        synchronized (lock) {
            sizeNotNull = items.size() > 0;
        }
        if (sizeNotNull) {
            synchronized (lock) {
                items.clear();
            }
            this.sizeUpdate.update(0);
            saveEmptyJournalFile();
        }
    }

    @Override
    public boolean delete(ILruCacheKey key) {
        if (items.remove(key) != null) {
            refreshSize();
            saveJournalFile();
            return true;
        }
        return false;
    }
}
