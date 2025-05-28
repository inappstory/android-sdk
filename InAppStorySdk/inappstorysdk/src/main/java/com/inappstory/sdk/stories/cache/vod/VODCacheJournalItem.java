package com.inappstory.sdk.stories.cache.vod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VODCacheJournalItem {
    boolean full() {
        return fullSize == downloadedSize;
    }

    String sha1;
    String uniqueKey;
    String ext;
    String replaceKey;
    long time;

    public void setFullSize(long fullSize) {
        this.fullSize = fullSize;
    }

    public long getFullSize() {
        return fullSize;
    }

    long fullSize;
    long downloadedSize;
    String mimeType;

    public String getUrl() {
        return url;
    }

    String url;

    List<VODCacheItemPart> parts = new ArrayList<>();

    public VODCacheJournalItem(
            String sha1,
            String uniqueKey,
            String ext,
            String replaceKey,
            List<VODCacheItemPart> parts,
            String mimeType,
            long fullSize,
            String url,
            long time
    ) {
        this.parts.addAll(parts);
        this.sha1 = sha1;
        this.uniqueKey = uniqueKey;
        this.ext = ext;
        this.replaceKey = replaceKey;
        this.mimeType = mimeType;
        this.url = url;
        this.fullSize = fullSize;
        this.time = time;
        combineParts();
        checkSize();
    }

    private void checkSize() {
        long size = 0;
        for (VODCacheItemPart part: parts) {
            size += part.end - part.start + 1;
        }
        downloadedSize = size;
    }

    public long getDownloadedSize() {
        checkSize();
        return downloadedSize;
    }

    private void combineParts() {
        List<VODCacheItemPart> newParts = new ArrayList<>();
        Iterator<VODCacheItemPart> partIterator = parts.iterator();
        while (partIterator.hasNext()) {
            VODCacheItemPart oldPart = partIterator.next();
            if (oldPart.end <= oldPart.start) continue;
            if (newParts.size() > 0) {
                VODCacheItemPart newPart = newParts.get(newParts.size() - 1);
                if (newPart.end >= oldPart.start - 1) {
                    if (newPart.end < oldPart.end) {
                        newPart.end = oldPart.end;
                    }
                } else {
                    newParts.add(oldPart);
                }
            } else {
                newParts.add(oldPart);
            }
        }
        parts = newParts;
    }

    public boolean hasPart(long from, long to) {
        updateTime();
        for (VODCacheItemPart part : parts) {
            if (part.start <= from && part.end >= to) return true;
        }
        return false;
    }

    public void addPart(long from, long to) {
        if (full()) return;
        if (hasPart(from, to)) return;
        int insertIndex = 0;
        for (VODCacheItemPart part : parts) {
            if (part.start >= from) break;
            insertIndex++;
        }
        parts.add(insertIndex, new VODCacheItemPart(from, to));
        combineParts();
        checkSize();
    }

    public void updateTime() {
        this.time = System.currentTimeMillis();
    }
}
