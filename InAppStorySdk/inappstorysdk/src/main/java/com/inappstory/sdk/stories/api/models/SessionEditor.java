package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.core.network.annotations.models.SerializedName;

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
    public HashMap<String, Object> config;
    public HashMap<String, String> messages;
}
