package com.inappstory.sdk.stories.cache.usecases;


import com.inappstory.sdk.stories.cache.vod.ContentRange;

public class StoryVODResourceFileUseCaseResult {
    public ContentRange range() {
        return range;
    }

    public byte[] bytes() {
        return bytes;
    }

    public boolean cached() {
        return cached;
    }

    private final ContentRange range;
    private final byte[] bytes;
    private final boolean cached;

    public StoryVODResourceFileUseCaseResult(ContentRange range, byte[] bytes, boolean cached) {
        this.range = range;
        this.bytes = bytes;
        this.cached = cached;
    }
}
