package com.inappstory.sdk.utils;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASContentLoader;
import com.inappstory.sdk.stories.cache.FilesDownloader;

import java.io.File;

public class FilePathCacheGenerator {
    private final String url;
    private final String uniqueKey;
    private final IASCore core;
    private final FilePathCacheType cacheType;

    public FilePathCacheGenerator(String url, IASCore core, FilePathCacheType cacheType) {
        this.url = url;
        this.core = core;
        this.cacheType = cacheType;
        this.uniqueKey = null;
    }

    public FilePathCacheGenerator(String url,
                                  String uniqueKey,
                                  IASCore core,
                                  FilePathCacheType cacheType
    ) {
        this.url = url;
        this.core = core;
        this.cacheType = cacheType;
        this.uniqueKey = uniqueKey;
    }

    public String generateSymlink(String gameName) {
        if (cacheType == FilePathCacheType.GAME_RESOURCE) {
            return core.cachesHolder().getInfiniteCache()
                    .getCacheDir().getAbsolutePath() +
                    File.separator +
                    "v2" +
                    File.separator +
                    "zip" +
                    File.separator +
                    gameName +
                    File.separator +
                    url +
                    File.separator +
                    "resources_" +
                    uniqueKey +
                    File.separator;
        }
        return null;
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
            case GAME_FOLDER:
                return core.cachesHolder().getInfiniteCache()
                        .getCacheDir()
                        .getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "zip" +
                        File.separator +
                        url +
                        File.separator +
                        uniqueKey;
            case GAME_RESOURCE:
                return core.cachesHolder().getInfiniteCache()
                        .getCacheDir()
                        .getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "gameResources" +
                        File.separator +
                        StringsUtils.md5(url);
            case ARCHIVE:
                return core.cachesHolder().getInfiniteCache()
                        .getCacheDir().getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "zip" +
                        File.separator +
                        this.url +
                        File.separator +
                        uniqueKey;
            case GAME_SPLASH:
                return core.cachesHolder().getInfiniteCache() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "games" +
                        File.separator +
                        "splashes" +
                        File.separator +
                        this.url;
            default:
                return null;
        }
    }
}
