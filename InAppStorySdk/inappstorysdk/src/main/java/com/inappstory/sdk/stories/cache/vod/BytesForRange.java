package com.inappstory.sdk.stories.cache.vod;

public class BytesForRange {
    private final ContentRange range;

    private final byte[] content;

    public BytesForRange(ContentRange range, byte[] content) {
        this.range = range;
        this.content = content;
    }

    public ContentRange range() {
        return range;
    }

    public byte[] content() {
        return content;
    }
}
