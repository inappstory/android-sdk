package com.inappstory.sdk.utils;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASContentLoader;
import com.inappstory.sdk.stories.cache.FilesDownloader;

import java.io.File;

public class FilePathCacheGenerator {
    private final String url;
    private final IASCore core;
    private final FilePathCacheType cacheType;

    public FilePathCacheGenerator(String url, IASCore core, FilePathCacheType cacheType) {
        this.url = url;
        this.core = core;
        this.cacheType = cacheType;
    }


    public String generate() {
        switch (cacheType) {
            case LAYOUT:
                return core.cachesHolder().getInfiniteCache()
                        .getCacheDir()
                        .getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "layout" +
                        File.separator +
                        StringsUtils.md5(url);
            case STORY_RESOURCE:
                return core.cachesHolder().getCommonCache()
                        .getCacheDir()
                        .getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "stories" +
                        File.separator +
                        "resources" +
                        File.separator +
                        StringsUtils.md5(url) +
                        StringsUtils.getExtensionFromUrl(url);
            case STORY_VOD_RESOURCE:
                return core.cachesHolder().getVodCache()
                        .getCacheDir()
                        .getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "stories" +
                        File.separator +
                        "resources" +
                        File.separator +
                        StringsUtils.md5(url) +
                        StringsUtils.getExtensionFromUrl(url);
            case SESSION_ASSET:
            case SESSION_ASSET_LOCAL:
                return core.cachesHolder().getBundleCache()
                        .getCacheDir()
                        .getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "bundle" +
                        File.separator +
                        url;
            case STORY_COVER:
                return core.cachesHolder().getFastCache()
                        .getCacheDir()
                        .getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "stories" +
                        File.separator +
                        "covers" +
                        File.separator +
                        StringsUtils.md5(url) +
                        StringsUtils.getExtensionFromUrl(url);
            case CUSTOM_FILE:
                return core.cachesHolder().getInfiniteCache()
                        .getCacheDir()
                        .getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "custom" +
                        File.separator +
                        StringsUtils.md5(url) +
                        StringsUtils.getExtensionFromUrl(url);
            case ARCHIVE:
            case GAME_FOLDER:
            case GAME_SPLASH:
            case GAME_RESOURCE:
            default:
                return null;
        }
    }
}
