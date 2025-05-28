package com.inappstory.sdk.stories.api.interfaces;

import com.inappstory.sdk.core.data.IGameArchiveItem;
import com.inappstory.sdk.stories.api.models.GameArchiveItem;
import com.inappstory.sdk.stories.api.models.GameSplashAnimation;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;
import com.inappstory.sdk.stories.api.models.WebResource;

import java.util.List;

public interface IGameCenterData {
    String id();

    GameSplashScreen splashScreen();

    GameSplashScreen splashLandscapeScreen();

    GameSplashAnimation splashAnimation();

    List<WebResource> resources();

    String url();

    Long archiveSize();

    String archiveSha1();

    Long archiveUncompressedSize();

    long getTotalSize();

    List<GameArchiveItem> archiveItems();

}
