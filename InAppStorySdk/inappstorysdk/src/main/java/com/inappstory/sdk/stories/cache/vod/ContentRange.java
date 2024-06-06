package com.inappstory.sdk.stories.cache.vod;

public class ContentRange {
    public ContentRange(long start, long end, long length) {
        this.start = start;
        this.end = end;
        this.length = length;
    }

    public long start() {
        return start;
    }

    public long end() {
        return end;
    }

    public long length() {
        return length;
    }

    long start = 0;
    long end = -1;
    long length = 0;
}
