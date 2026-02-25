package com.inappstory.sdk.refactoring.session.data.network;

import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;
import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;

public class NSession {
    @SerializedName(NSessionResponseFields.session)
    public NSessionId sessionId;

    @SerializedName(NSessionResponseFields.isAllowUgc)
    public boolean isAllowUgc;
    @SerializedName(NSessionResponseFields.serverTimestamp)
    public Long serverTimestamp;
    @SerializedName(NSessionResponseFields.preloadGame)
    public boolean preloadGame;
    @SerializedName(NSessionResponseFields.previewAspectRatio)
    public float previewAspectRatio;
    @SerializedName(NSessionResponseFields.sessionAssets)
    public List<NSessionAsset> sessionAssets;

    @SerializedName(NSessionResponseFields.isAllowProfiling)
    public boolean isAllowProfiling;
    @SerializedName(NSessionResponseFields.isAllowStatV1)
    public Boolean isAllowStatV1;
    @SerializedName(NSessionResponseFields.isAllowStatV2)
    public Boolean isAllowStatV2;
    @SerializedName(NSessionResponseFields.isAllowCrash)
    public Boolean isAllowCrash;

    @SerializedName(NSessionResponseFields.placeholders)
    public List<NSessionPlaceholder> placeholders;

    @SerializedName(NSessionResponseFields.imagePlaceholders)
    public List<NSessionPlaceholder> imagePlaceholders;
}
