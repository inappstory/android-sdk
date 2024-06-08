package com.inappstory.sdk.stories.cache.vod;

import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.utils.CollectionUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VODCacheJournal {
    private Map<String, VODCacheJournalItem> vodItems = new HashMap<>();

    public static final int VERSION = 1;
    private File journalFile;
    private final Object lock = new Object();

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

    }

    public void writeJournal() {
        List<VODCacheJournalItem> list;
        synchronized (lock) {
            list = new ArrayList<>(vodItems.values());
        }
        DataOutputStream stream = null;
        try {
            stream = new DataOutputStream(new FileOutputStream(journalFile));
            stream.writeShort(VERSION);
            stream.writeInt(list.size());
            for (VODCacheJournalItem item : list) {
                stream.writeUTF(item.uniqueKey);
                stream.writeUTF(item.url);
                stream.writeLong(item.fullSize);
                stream.writeLong(item.downloadedSize);
                stream.writeLong(item.time);
                stream.writeInt(item.parts.size());
                for (VODCacheItemPart vodCacheItemPart: item.parts) {
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
