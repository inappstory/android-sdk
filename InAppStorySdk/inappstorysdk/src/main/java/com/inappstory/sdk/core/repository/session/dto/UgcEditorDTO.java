package com.inappstory.sdk.core.repository.session.dto;

import com.inappstory.sdk.stories.api.models.SessionEditor;
import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UgcEditorDTO {
    public String url;
    public String urlTemplate;
    public String versionTemplate;
    public List<UGCVersionToSDKBuild> versionsMap;
    public HashMap<String, Object> config;
    public HashMap<String, String> messages;

    public UgcEditorDTO(SessionEditor sessionEditor) {
        this.url = sessionEditor.url;
        this.urlTemplate = sessionEditor.urlTemplate;
        this.versionTemplate = sessionEditor.versionTemplate;
        if (versionsMap != null) {
            this.versionsMap = new ArrayList<>(versionsMap);
        } else {
            this.versionsMap = new ArrayList<>();
        }
        if (config != null) {
            this.config = new HashMap<>(config);
        } else {
            this.config = new HashMap<>();
        }
        if (messages != null) {
            this.messages = new HashMap<>(messages);
        } else {
            this.messages = new HashMap<>();
        }
    }
}
