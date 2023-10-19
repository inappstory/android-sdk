package com.inappstory.sdk.ugc.extinterfaces;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.HashMap;
import java.util.List;

public interface IUgcEditor {
    @SerializedName("url")
    String url();
    @SerializedName("urlTemplate")
    String urlTemplate();
    @SerializedName("versionTemplate")
    String versionTemplate();
    @SerializedName("versionsMap")
    List<IUgcVersionToSDKBuild> versionsMap();
    HashMap<String, Object> config();
    HashMap<String, String> messages();
}
