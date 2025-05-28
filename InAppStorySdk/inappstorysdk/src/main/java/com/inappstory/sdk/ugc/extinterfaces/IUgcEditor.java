package com.inappstory.sdk.ugc.extinterfaces;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.HashMap;
import java.util.List;

public interface IUgcEditor {
    String session();
    String url();
    String urlTemplate();
    String versionTemplate();
    List<IUgcVersionToSDKBuild> versionsMap();
    HashMap<String, Object> config();
    HashMap<String, String> messages();
}
