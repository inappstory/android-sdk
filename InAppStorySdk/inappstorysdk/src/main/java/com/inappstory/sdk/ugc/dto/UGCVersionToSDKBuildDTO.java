package com.inappstory.sdk.ugc.dto;

import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;
import com.inappstory.sdk.ugc.extinterfaces.IUgcVersionToSDKBuild;

public class UGCVersionToSDKBuildDTO implements IUgcVersionToSDKBuild {
    private int minBuild;
    private String editor;

    @Override
    public int minBuild() {
        return minBuild;
    }

    @Override
    public String editor() {
        return editor;
    }

    public UGCVersionToSDKBuildDTO(UGCVersionToSDKBuild build) {
        this.minBuild = build.minBuild;
        this.editor = build.editor;
    }
}