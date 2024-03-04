package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;
import java.util.Map;

public class GameCenterData {
    @SerializedName("id")
    public String id;
    @SerializedName("splashScreen")
    public GameSplashScreen splashScreen;
    @SerializedName("resources")
    public List<WebResource> resources;
    @SerializedName("options")
    public GameScreenOptions options;
    @Required
    @SerializedName("downloadUrl")
    public String url;
    @Required
    @SerializedName("initCode")
    public String initCode;

    @SerializedName("canTryReloadCount")
    public Integer canTryReloadCount;

    @SerializedName("archiveSize")
    public Long archiveSize;

    @SerializedName("archiveSha1")
    public String archiveSha1;

    @SerializedName("instanceUserData")
    public Map<String, Object> instanceUserData;

    @SerializedName("archiveUncompressedSize")
    public Long archiveUncompressedSize;

    public int canTryReloadCount() {
        if (canTryReloadCount == null)
             return 5;
        return canTryReloadCount;
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
}
