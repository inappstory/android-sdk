package com.inappstory.sdk.lrudiskcache;

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
import java.util.Set;

public class CacheJournal {
    private long currentSize;
    FileManager fileManager;

    public static final int VERSION = 1;
    private File journalFile;

    private Object lock = new Object();

    private final Map<String, CacheJournalItem> itemsLinks = new HashMap<>();

    public void put(CacheJournalItem item, long cacheSize) throws IOException {
        long fileSize = item.getSize();
        removeOld(fileSize, cacheSize);
        putLink(item);
    }

    public CacheJournal(FileManager fileManager) {
        this.fileManager = fileManager;
        readJournal();
    }

    private void putLink(CacheJournalItem item) {
        itemsLinks.put(item.getKey(), item);
        currentSize += item.getSize();
    }

    public long getCurrentCacheSize() {
        return currentSize;
    }

    public long getJournalSize() {
        return journalFile.length();
    }

    private void setCurrentCacheSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public CacheJournalItem get(String key) {
        CacheJournalItem item = itemsLinks.get(key);
        updateTime(item);
        return item;
    }

    public CacheJournalItem delete(String key, boolean withFile) throws IOException {
        CacheJournalItem item = itemsLinks.remove(key);
        if (item != null) {
            currentSize -= item.getSize();
            if (withFile) fileManager.delete(item.getName());
        }
        return item;
    }



    public Set<String> keySet() {
        return Collections.unmodifiableSet(itemsLinks.keySet());
    }

    private void updateTime(CacheJournalItem item) {
        if (item != null) {
            long time = System.currentTimeMillis();
            item.setTime(time);
        }
    }


    public void writeJournal() {
        synchronized (lock) {
            DataOutputStream stream = null;
            try {
                stream = new DataOutputStream(new FileOutputStream(journalFile));
                stream.writeShort(VERSION);
                stream.writeInt(itemsLinks.size());
                for (CacheJournalItem item : itemsLinks.values()) {
                    stream.writeUTF(item.getKey());
                    stream.writeUTF(item.getName());
                    stream.writeLong(item.getTime());
                    stream.writeLong(item.getSize());
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

    private void removeOld(long newFileSize, long limitSize) throws IOException {
        if (currentSize + newFileSize > limitSize) {
            List<CacheJournalItem> items = new ArrayList<>(itemsLinks.values());
            Collections.sort(items, new Utils.TimeComparator());
            for (int i = items.size() - 1; i > 0; i--) {
                CacheJournalItem item = items.remove(i);
                fileManager.delete(item.getName());
                itemsLinks.remove(item.getKey());
                currentSize -= item.getSize();
                if (currentSize + newFileSize < limitSize) {
                    break;
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
                    throw new IllegalArgumentException("Invalid journal format version");
                }
                int count = stream.readInt();
                long currentSize = 0;
                for (int c = 0; c < count; c++) {
                    String key = stream.readUTF();
                    String name = stream.readUTF();
                    long time = stream.readLong();
                    long size = stream.readLong();
                    currentSize += size;
                    CacheJournalItem item = new CacheJournalItem(key, name, time, size);
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
