package com.inappstory.sdk.ugc.dto;

import com.inappstory.sdk.core.network.content.models.SessionEditor;
import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;
import com.inappstory.sdk.ugc.extinterfaces.IUgcEditor;
import com.inappstory.sdk.ugc.extinterfaces.IUgcVersionToSDKBuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionEditorDTO implements IUgcEditor {

    public String url;
    public String urlTemplate;
    public String versionTemplate;
    private String sessionId;
    public List<IUgcVersionToSDKBuild> versionsMap;

    public HashMap<String, Object> config;
    public HashMap<String, String> messages;

    public SessionEditorDTO(SessionEditor editor, String sessionId) {
        this.url = editor.url;
        this.sessionId = sessionId;
        this.urlTemplate = editor.urlTemplate;
        this.versionTemplate = editor.versionTemplate;
        List<IUgcVersionToSDKBuild> dtoMap = null;
        if (editor.versionsMap != null) {
            dtoMap = new ArrayList<>();
            for (UGCVersionToSDKBuild sdkBuild : editor.versionsMap) {
                dtoMap.add(new UGCVersionToSDKBuildDTO(sdkBuild));
            }
        }
        this.versionsMap = dtoMap;
        if (editor.config != null)
            this.config = new HashMap<>(editor.config);
        if (editor.messages != null)
            this.messages = new HashMap<>(editor.messages);
    }

    @Override
    public String session() {
        return sessionId;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String urlTemplate() {
        return urlTemplate;
    }

    @Override
    public String versionTemplate() {
        return versionTemplate;
    }

    @Override
    public List<IUgcVersionToSDKBuild> versionsMap() {
        return versionsMap;
    }

    @Override
    public HashMap<String, Object> config() {
        return config;
    }

    @Override
    public HashMap<String, String> messages() {
        return messages;
    }
}