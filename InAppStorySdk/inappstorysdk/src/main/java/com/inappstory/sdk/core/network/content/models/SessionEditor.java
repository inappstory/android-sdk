package com.inappstory.sdk.core.network.content.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionEditor {

    @SerializedName(ModelFields.SessionEditor.url)
    public String url;
    @SerializedName(ModelFields.SessionEditor.urlTemplate)
    public String urlTemplate;
    @SerializedName(ModelFields.SessionEditor.versionTemplate)
    public String versionTemplate;
    @SerializedName(ModelFields.SessionEditor.versionsMap)
    public List<UGCVersionToSDKBuild> versionsMap;
    @SerializedName(ModelFields.SessionEditor.config)
    public Map<String, Object> config;
    @SerializedName(ModelFields.SessionEditor.messages)
    public Map<String, String> messages;
}
