package com.inappstory.sdk.stories.api.models;

import android.view.View;

import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameCenterData implements IGameCenterData {
    @SerializedName(ModelFields.GameCenterData.id)
    public String id;
    @SerializedName(ModelFields.GameCenterData.splashScreen)
    public GameSplashScreen splashScreen;
    @SerializedName(ModelFields.GameCenterData.splashLandscapeScreen)
    public GameSplashScreen splashLandscapeScreen;
    @SerializedName(ModelFields.GameCenterData.splashAnimation)
    public GameSplashAnimation splashAnimation;
    @SerializedName(ModelFields.GameCenterData.resources)
    public List<WebResource> resources;
    @SerializedName(ModelFields.GameCenterData.options)
    public GameScreenOptions options;
    @SerializedName(ModelFields.GameCenterData.gameLaunchConditions)
    public GameLaunchConditions gameLaunchConditions;
    @SerializedName(ModelFields.GameCenterData.archiveItems)
    public List<GameArchiveItem> archiveItems;
    @SerializedName(ModelFields.GameCenterData.layoutDirection)
    public String layoutDirection;
    @SerializedName(ModelFields.GameCenterData.loggerLevel)
    public Integer loggerLevel;
    @Required
    @SerializedName(ModelFields.GameCenterData.url)
    public String url;
    @Required
    @SerializedName(ModelFields.GameCenterData.initCode)
    public String initCode;
    @SerializedName(ModelFields.GameCenterData.canTryReloadCount)
    public Integer canTryReloadCount;
    @SerializedName(ModelFields.GameCenterData.archiveSize)
    public Long archiveSize;
    @SerializedName(ModelFields.GameCenterData.archiveSha1)
    public String archiveSha1;
    @SerializedName(ModelFields.GameCenterData.instanceUserData)
    public Map<String, Object> instanceUserData;
    @SerializedName(ModelFields.GameCenterData.archiveUncompressedSize)
    public Long archiveUncompressedSize;

    @Override
    public String layoutDirectionString() {
        return Objects.equals(layoutDirection, "rtl") ? "rtl" : "ltr";
    }

    @Override
    public int layoutDirectionRaw() {
        return Objects.equals(layoutDirection, "rtl") ?
                View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR;
    }

    @Override
    public GameSplashScreen splashLandscapeScreen() {
        return splashLandscapeScreen;
    }

    @Override
    public GameSplashAnimation splashAnimation() {
        return splashAnimation;
    }

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

    @Override
    public List<GameArchiveItem> archiveItems() {
        return archiveItems != null ? archiveItems : new ArrayList<GameArchiveItem>();
    }
}
