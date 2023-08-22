package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;

import java.io.File;

public class RemoveOldGameFilesUseCase extends GameNameHolder {
    public RemoveOldGameFilesUseCase(String newUrl) {
        this.newUrl = newUrl;
    }

    String newUrl;

    void remove() {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService == null) {
            return;
        }
        LruDiskCache cache = inAppStoryService.getInfiniteCache();
        String gameName = getGameName(newUrl);
        File gameDir = new File(
                cache.getCacheDir() +
                        File.separator + "zip" +
                        File.separator + gameName +
                        File.separator
        );
        if (!gameDir.getAbsolutePath().startsWith(
                cache.getCacheDir() +
                        File.separator + "zip")) {
            return;
        }
        if (gameDir.exists() && gameDir.isDirectory()) {
            for (File gameDirFile : gameDir.listFiles()) {
                if (gameDirFile.getAbsolutePath().contains("" + newUrl.hashCode()))
                    continue;
                FileManager.deleteRecursive(gameDirFile);
            }
        }
    }
}
