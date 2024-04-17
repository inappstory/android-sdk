package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;

import java.util.List;

public class PreloadGameCenterData implements IGameCenterData {
    @Required
    @SerializedName("id")
    public String id;
    @SerializedName("splashScreen")
    public GameSplashScreen splashScreen;
    @SerializedName("resources")
    public List<WebResource> resources;
    @SerializedName("downloadUrl")
    public String url;

    @SerializedName("archiveSize")
    public Long archiveSize;

    @SerializedName("archiveSha1")
    public String archiveSha1;

    @SerializedName("archiveUncompressedSize")
    public Long archiveUncompressedSize;

    @Override
    public String id() {
        return id;
    }

    @Override
    public GameSplashScreen splashScreen() {
        return splashScreen;
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

    public void updateData(IGameCenterData gameCenterData) {
        this.splashScreen = gameCenterData.splashScreen();
        this.url = gameCenterData.url();
        this.resources = gameCenterData.resources();
        this.archiveSha1 = gameCenterData.archiveSha1();
        this.archiveSize = gameCenterData.archiveSize();
        this.archiveUncompressedSize = gameCenterData.archiveUncompressedSize();
    }
}
