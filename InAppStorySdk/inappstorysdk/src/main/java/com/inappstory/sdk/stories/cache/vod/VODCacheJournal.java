package com.inappstory.sdk.stories.cache.vod;

import android.util.Log;

import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.utils.CollectionUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VODCacheJournal {
    private final Map<String, VODCacheJournalItem> vodItems = new HashMap<>();

    public static final int VERSION = 1;
    private final File journalFile;
    private final Object lock = new Object();

    public void clear() {
        vodItems.clear();
        writeJournal();
    }

    public VODCacheJournal(File journalFile) {
        if (!journalFile.exists()) {
            try {
                journalFile.getParentFile().mkdirs();
                journalFile.createNewFile();
            } catch (Exception ignored) {

            }
        }
        this.journalFile = journalFile;
        readJournal();
    }

    public VODCacheJournalItem getItem(String key) {
        synchronized (lock) {
            return vodItems.get(key);
        }
    }

    public void putItem(VODCacheJournalItem item) {
        synchronized (lock) {
            vodItems.put(item.uniqueKey, item);
        }
        writeJournal();
    }

    public void readJournal() {
        synchronized (lock) {
            if (journalFile == null) return;
            if (!journalFile.exists()) return;
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
                int itemsCount = stream.readInt();
                for (int itemIndex = 0; itemIndex < itemsCount; itemIndex++) {
                    String uniqueKey = stream.readUTF();
                    String url = stream.readUTF();
                    long fullSize = stream.readLong();
                    long time = stream.readLong();
                    int partsCount = stream.readInt();
                    List<VODCacheItemPart> parts = new ArrayList<>();
                    for (int partIndex = 0; partIndex < partsCount; partIndex++) {
                        long start = stream.readLong();
                        long end = stream.readLong();
                        parts.add(new VODCacheItemPart(start, end));
                    }
                    vodItems.put(
                            uniqueKey,
                            new VODCacheJournalItem(
                                    "",
                                    uniqueKey,
                                    "",
                                    "",
                                    parts,
                                    "",
                                    fullSize,
                                    url,
                                    time
                            )
                    );
                }
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

    public void writeJournal() {
        synchronized (lock) {
            if (journalFile == null) return;
            if (!journalFile.exists()) return;
            List<VODCacheJournalItem> list = new ArrayList<>(vodItems.values());
            DataOutputStream stream = null;
            try {
                stream = new DataOutputStream(new FileOutputStream(journalFile));
                stream.writeShort(VERSION);
                stream.writeInt(list.size());
                for (VODCacheJournalItem item : list) {
                    stream.writeUTF(item.uniqueKey);
                    stream.writeUTF(item.url);
                    stream.writeLong(item.fullSize);
                    stream.writeLong(item.time);
                    stream.writeInt(item.parts.size());
                    for (VODCacheItemPart vodCacheItemPart : item.parts) {
                        stream.writeLong(vodCacheItemPart.start);
                        stream.writeLong(vodCacheItemPart.end);
                    }
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
}
