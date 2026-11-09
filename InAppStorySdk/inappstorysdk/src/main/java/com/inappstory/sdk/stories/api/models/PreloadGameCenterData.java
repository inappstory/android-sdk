package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;

import java.util.ArrayList;
import java.util.List;

public class PreloadGameCenterData implements IGameCenterData {
    @Required
    @SerializedName(ModelFields.PreloadGameCenterData.id)
    public String id;
    @SerializedName(ModelFields.PreloadGameCenterData.splashScreen)
    public GameSplashScreen splashScreen;
    @SerializedName(ModelFields.PreloadGameCenterData.splashLandscapeScreen)
    public GameSplashScreen splashLandscapeScreen;
    @SerializedName(ModelFields.PreloadGameCenterData.splashAnimation)
    public GameSplashAnimation splashAnimation;
    @SerializedName(ModelFields.PreloadGameCenterData.resources)
    public List<WebResource> resources;
    @Required
    @SerializedName(ModelFields.PreloadGameCenterData.url)
    public String url;
    @SerializedName(ModelFields.PreloadGameCenterData.archiveSize)
    public Long archiveSize;
    @SerializedName(ModelFields.PreloadGameCenterData.archiveSha1)
    public String archiveSha1;
    @SerializedName(ModelFields.PreloadGameCenterData.archiveUncompressedSize)
    public Long archiveUncompressedSize;
    @SerializedName(ModelFields.PreloadGameCenterData.archiveItems)
    public List<GameArchiveItem> archiveItems;

    @Override
    public String id() {
        return id;
    }

    @Override
    public GameSplashScreen splashScreen() {
        return splashScreen;
    }

    @Override
    public GameSplashScreen splashLandscapeScreen() {
        return splashLandscapeScreen;
    }

    @Override
    public String layoutDirectionString() {
        return null;
    }

    @Override
    public int layoutDirectionRaw() {
        return 0;
    }

    @Override
    public GameSplashAnimation splashAnimation() {
        return splashAnimation;
    }

    @Override
    public List<WebResource> resources() {
        return resources;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public Long archiveSize() {
        return archiveSize;
    }

    @Override
    public String archiveSha1() {
        return archiveSha1;
    }

    @Override
    public Long archiveUncompressedSize() {
        return archiveUncompressedSize;
    }

    public long getTotalSize() {
        long totalSize = 0;
        if (archiveUncompressedSize != null && archiveUncompressedSize > 0
                && archiveSize != null && archiveSize > 0)
            totalSize += archiveUncompressedSize + archiveSize;
        else
            return 0;
        if (splashScreen != null && splashScreen.size != null) totalSize += splashScreen.size;
        if (resources != null) {
            for (WebResource resource : resources) {
                totalSize += resource.size;
            }
        }
        return totalSize;
    }

    @Override
    public List<GameArchiveItem> archiveItems() {
        return archiveItems != null ? archiveItems : new ArrayList<GameArchiveItem>();
    }

    public void updateData(IGameCenterData gameCenterData) {
        this.splashScreen = gameCenterData.splashScreen();
        this.url = gameCenterData.url();
        this.resources = gameCenterData.resources();
        this.archiveSha1 = gameCenterData.archiveSha1();
        this.archiveSize = gameCenterData.archiveSize();
        this.archiveUncompressedSize = gameCenterData.archiveUncompressedSize();
    }
}
