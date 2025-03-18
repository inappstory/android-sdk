package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;

import java.util.List;
import java.util.Map;

public class GameCenterData implements IGameCenterData {
    @SerializedName("id")
    public String id;
    @SerializedName("splashScreen")
    public GameSplashScreen splashScreen;
    @SerializedName("splashLandscapeScreen")
    public GameSplashScreen splashLandscapeScreen;
    @SerializedName("splashLottieScreen")
    public GameSplashAnimation splashAnimation;
    @SerializedName("resources")
    public List<WebResource> resources;
    @SerializedName("screenOptions")
    public GameScreenOptions options;
    @SerializedName("loggerLevel")
    public Integer loggerLevel;
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


    @Override
    public GameSplashScreen splashLandscapeScreen() {
        return splashLandscapeScreen;
    }

    @Override
    public GameSplashAnimation splashAnimation() {
        return splashAnimation;
    }

    @SerializedName("instanceUserData")
    public Map<String, Object> instanceUserData;

    @SerializedName("archiveUncompressedSize")
    public Long archiveUncompressedSize;

    public int loggerLevel() {
        if (loggerLevel == null) return 0;
        return loggerLevel;
    }

    public int canTryReloadCount() {
        if (canTryReloadCount == null)
            return 5;
        return canTryReloadCount;
    }

    public String id() {
        return id;
    }

    public GameSplashScreen splashScreen() {
        return splashScreen;
    }

    public List<WebResource> resources() {
        return resources;
    }

    public GameScreenOptions options() {
        return options;
    }

    public String url() {
        return url;
    }

    public String initCode() {
        return initCode;
    }

    public Long archiveSize() {
        return archiveSize;
    }

    public String archiveSha1() {
        return archiveSha1;
    }

    public Map<String, Object> instanceUserData() {
        return instanceUserData;
    }

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
}
