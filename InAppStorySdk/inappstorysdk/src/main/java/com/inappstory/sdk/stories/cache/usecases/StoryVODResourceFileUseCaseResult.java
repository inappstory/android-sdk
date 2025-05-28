package com.inappstory.sdk.stories.cache.usecases;


import com.inappstory.sdk.stories.cache.vod.ContentRange;

import java.io.File;

public class StoryVODResourceFileUseCaseResult {
    public ContentRange range() {
        return range;
    }

    public File file() {
        return file;
    }

    public boolean cached() {
        return cached;
    }

    private final ContentRange range;
    private final File file;
    private final boolean cached;

    public StoryVODResourceFileUseCaseResult(ContentRange range, File file, boolean cached) {
        this.range = range;
        this.file = file;
        this.cached = cached;
    }
}
