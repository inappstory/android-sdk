package com.inappstory.sdk.lrudiskcache;

import android.util.Log;

import com.inappstory.sdk.utils.CollectionUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CacheJournal {
    private long currentSize;
    FileManager fileManager;

    public static final int VERSION = 1;
    private File journalFile;

    private final Object lock = new Object();

    private final Map<String, CacheJournalItem> itemsLinks = new HashMap<>();

    private final Map<String, List<CacheJournalItem>> cacheItems = new HashMap<>();

    public void put(CacheJournalItem item, long cacheSize) throws IOException {
        long fileSize = item.getSize();
        if (fileSize > cacheSize) return;
        removeOld(fileSize, cacheSize);
        putLink(item);
    }

    public CacheJournal(FileManager fileManager) {
        this.fileManager = fileManager;
        readJournal();
    }


    private CacheJournalItem getItemFromCacheItems(String uniqueKey, String mimeType) {
        List<CacheJournalItem> current = cacheItems.get(uniqueKey);
        if (current != null && current.size() > 0) {
            if (mimeType != null) {
                for (CacheJournalItem item : current) {
                    if (Objects.equals(item.getMimeType(), mimeType)) {
                        return item;
                    }
                }
                return null;
            }
            return current.get(0);
        }
        return null;
    }

    private void putItemToCacheItems(CacheJournalItem item) {
        if (cacheItems.get(item.getUniqueKey()) == null)
            cacheItems.put(
                    item.getUniqueKey(),
                    new ArrayList<CacheJournalItem>()
            );
        cacheItems.get(item.getUniqueKey()).add(item);
    }

    private void putLink(CacheJournalItem item) {
        putItemToCacheItems(item);
        currentSize += item.getSize();
    }


    private void setCurrentCacheSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public CacheJournalItem get(String uniqueKey) {
        CacheJournalItem item = getItemFromCacheItems(uniqueKey, null);
        updateTime(item);
        return item;
    }

    public CacheJournalItem get(String uniqueKey, String mimeType) {
        CacheJournalItem item = getItemFromCacheItems(uniqueKey, mimeType);
        updateTime(item);
        return item;
    }

    public boolean delete(String key, boolean withFile) throws IOException {
        return delete(key, null, withFile);
    }


    public boolean delete(String key, String mimeType, boolean withFile) throws IOException {
        List<CacheJournalItem> items = cacheItems.remove(key);
        boolean res = false;
        if (items != null) {
            for (CacheJournalItem item : items) {
                if (mimeType == null || Objects.equals(mimeType, item.getMimeType())) {
                    currentSize -= item.getSize();
                    if (withFile) fileManager.delete(item.getFilePath());
                    res = true;
                }
            }
        }
        return res;
    }


    public Set<String> keySet() {
        return Collections.unmodifiableSet(cacheItems.keySet());
    }

    private void updateTime(CacheJournalItem item) {
        if (item != null) {
            long time = System.currentTimeMillis();
            item.setTime(time);
        }
    }


    private void removeOld(long newFileSize, long limitSize) throws IOException {
        if (currentSize + newFileSize > limitSize) {
            List<CacheJournalItem> items = CollectionUtils.mapOfArraysToArrayList(cacheItems);
            Collections.sort(items, new Utils.TimeComparator());
            for (int i = items.size() - 1; i > 0; i--) {
                CacheJournalItem item = items.remove(i);
                fileManager.delete(item.getFilePath());
                cacheItems.remove(item.getUniqueKey());
                currentSize -= item.getSize();
                if (currentSize + newFileSize < limitSize) {
                    break;
                }
            }
        }
    }

    public void writeJournal() {
        synchronized (lock) {
            DataOutputStream stream = null;
            try {
                stream = new DataOutputStream(new FileOutputStream(journalFile));
                stream.writeShort(VERSION);
                List<CacheJournalItem> list = CollectionUtils.mapOfArraysToArrayList(cacheItems);
                stream.writeInt(list.size());
                for (CacheJournalItem item : list) {
                    stream.writeUTF(item.getUniqueKey());
                    stream.writeUTF(item.getFilePath());
                    stream.writeUTF(item.getMimeType());
                    stream.writeUTF(item.getExt());
                    stream.writeUTF(item.getSha1());
                    stream.writeUTF(item.getReplaceKey());
                    stream.writeLong(item.getTime());
                    stream.writeLong(item.getSize());
                    stream.writeLong(item.getDownloadedSize());
                }
            } catch (IOException ex) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    private void readJournal() {
        synchronized (lock) {
            journalFile = fileManager.getJournalFile();
            if (journalFile.length() == 0) return;
            DataInputStream stream = null;
            try {
                stream = new DataInputStream(new FileInputStream(journalFile));
                int version = stream.readShort();
                if (version != VERSION) {
                    Log.d("InAppStory_SDK_error", "Invalid journal " +
                            journalFile.getCanonicalPath() + " format version");
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                    }
                    return;
                }
                int count = stream.readInt();
                long currentSize = 0;
                for (int c = 0; c < count; c++) {
                    String uniqueKey = stream.readUTF();
                    String filePath = stream.readUTF();
                    String mimeType = stream.readUTF();
                    String ext = stream.readUTF();
                    String sha1 = stream.readUTF();
                    String replaceKey = stream.readUTF();
                    long time = stream.readLong();
                    long size = stream.readLong();
                    currentSize += size;
                    long downloadedSize = stream.readLong();
                    CacheJournalItem item = new CacheJournalItem(
                            uniqueKey,
                            filePath,
                            ext,
                            mimeType,
                            sha1,
                            replaceKey,
                            time,
                            size,
                            downloadedSize
                    );
                    putLink(item);
                }
                setCurrentCacheSize(currentSize);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }
}
