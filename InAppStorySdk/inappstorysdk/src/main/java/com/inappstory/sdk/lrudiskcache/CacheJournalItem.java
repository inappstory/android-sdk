package com.inappstory.sdk.lrudiskcache;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class CacheJournalItem implements Serializable {
    public CacheJournalItem(
            String uniqueKey,
            String filePath,
            String ext,
            String mimeType,
            String sha1,
            String replaceKey,
            long time,
            long size,
            long downloadedSize
    ) {
        this.uniqueKey = uniqueKey;
        this.filePath = filePath;
        this.ext = ext;
        this.mimeType = mimeType;
        this.sha1 = sha1;
        this.replaceKey = replaceKey;
        this.time = time;
        this.size = size;
        this.downloadedSize = downloadedSize;
    }

    private String uniqueKey;
    private String filePath;
    private String ext;
    private String mimeType;
    private String sha1;
    private String replaceKey;
    private long time;
    private long size;
    private long downloadedSize;

    public String getExt() {
        return ext != null ? ext : "";
    }

    @NonNull
    public String getMimeType() {
        return mimeType != null ? mimeType : "";
    }

    @NonNull
    public String getSha1() {
        return sha1 != null ? sha1 : "";
    }

    @NonNull
    public String getReplaceKey() {
        return replaceKey != null ? replaceKey : "";
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isReady() {
        return downloadedSize == size;
    }


  /*  public CacheJournalItem(String uniqueKey, String filePath, long time, long size, long downloadedSize) {
        this.uniqueKey = uniqueKey;
        this.filePath = filePath;
        Log.e("CacheJournalItem", "Data:\nKey:" + uniqueKey + "\nName: " + filePath);
        this.time = time;
        this.size = size;
        this.downloadedSize = downloadedSize;
    }*/



    public String getUniqueKey() {
        return uniqueKey;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheJournalItem item = (CacheJournalItem) o;
        if (time != item.time) return false;
        if (Objects.equals(sha1, item.sha1)) return false;
        if (Objects.equals(uniqueKey, item.uniqueKey)) return false;
        if (Objects.equals(mimeType, item.mimeType)) return false;
        if (Objects.equals(replaceKey, item.replaceKey)) return false;
        return filePath.equals(item.filePath);
    }

    @Override
    public int hashCode() {
        int result = getUniqueKey().hashCode();
        result = 31 * result + getFilePath().hashCode();
        result = 31 * result + getMimeType().hashCode();
        result = 31 * result + getExt().hashCode();
        result = 31 * result + getReplaceKey().hashCode();
        result = 31 * result + getSha1().hashCode();
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }
}
