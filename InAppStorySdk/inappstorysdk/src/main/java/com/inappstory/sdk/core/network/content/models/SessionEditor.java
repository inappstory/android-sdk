package com.inappstory.sdk.core.network.content.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;

import java.util.HashMap;
import java.util.List;

public class SessionEditor {

    @SerializedName("url")
    public String url;
    @SerializedName("urlTemplate")
    public String urlTemplate;
    @SerializedName("versionTemplate")
    public String versionTemplate;
    @SerializedName("versionsMap")
    public List<UGCVersionToSDKBuild> versionsMap;

    @SerializedName("config")
    public HashMap<String, Object> config;

    @SerializedName("messages")
    public HashMap<String, String> messages;
}
