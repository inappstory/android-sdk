package com.inappstory.sdk.game.cache;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;

import java.io.File;

public class RemoveOldGameFilesUseCase extends GameNameHolder {
    public RemoveOldGameFilesUseCase(String newUrl) {
        this.newUrl = newUrl;
    }

    String newUrl;

    void remove() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                LruDiskCache cache = service.getInfiniteCache();
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
                    File[] files = gameDir.listFiles();
                    if (files == null) return;
                    for (File gameDirFile : files) {
                        if (gameDirFile.getAbsolutePath().contains("" + newUrl.hashCode()))
                            continue;
                        FileManager.deleteRecursive(gameDirFile);
                    }
                }
            }
        });

    }
}
