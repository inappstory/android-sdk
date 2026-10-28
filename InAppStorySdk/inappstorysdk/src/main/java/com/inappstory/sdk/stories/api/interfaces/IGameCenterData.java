package com.inappstory.sdk.stories.api.interfaces;

import android.view.View;

import com.inappstory.sdk.core.data.IGameArchiveItem;
import com.inappstory.sdk.stories.api.models.GameArchiveItem;
import com.inappstory.sdk.stories.api.models.GameSplashAnimation;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;
import com.inappstory.sdk.stories.api.models.WebResource;

import java.util.List;
import java.util.Objects;

public interface IGameCenterData {
    String id();

    GameSplashScreen splashScreen();

    GameSplashScreen splashLandscapeScreen();

    String layoutDirectionString();

    int layoutDirectionRaw();

    GameSplashAnimation splashAnimation();

    List<WebResource> resources();

    String url();

    Long archiveSize();

    String archiveSha1();

    Long archiveUncompressedSize();

    long getTotalSize();

    List<GameArchiveItem> archiveItems();

}
