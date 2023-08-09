package com.inappstory.sdk.stories.api.models;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.network.Required;
import com.inappstory.sdk.network.SerializedName;

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

    @SerializedName("archiveSize")
    public Long archiveSize;

    @SerializedName("archiveSha1")
    public String archiveSha1;

    @SerializedName("instanceUserData")
    public Map<String, Object> instanceUserData;

    public long getTotalSize() {
        long totalSize = archiveSize != null ? archiveSize : 0;
        if (splashScreen != null && splashScreen.size != null) totalSize += splashScreen.size;
        if (resources != null) {
            for (WebResource resource: resources) {
                totalSize += resource.size;
            }
        }
        return totalSize;
    }
}
