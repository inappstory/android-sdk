package com.inappstory.sdk.stories.cache.vod;

public class VODCacheItemPart {
    public VODCacheItemPart(long start, long end) {
        this.start = start;
        this.end = end;
    }

    long start;
    long end;
}
