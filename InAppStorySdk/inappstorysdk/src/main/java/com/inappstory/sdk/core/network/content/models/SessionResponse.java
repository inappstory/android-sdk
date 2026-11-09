package com.inappstory.sdk.core.network.content.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.List;

/**
 * Created by paperrose on 19.02.2018.
 */

public class SessionResponse {


    @SerializedName(ModelFields.SessionResponse.isAllowUgc)
    public boolean isAllowUgc;
    @SerializedName(ModelFields.SessionResponse.serverTimestamp)
    public Long serverTimestamp;
    @SerializedName(ModelFields.SessionResponse.preloadGame)
    public boolean preloadGame;
    @SerializedName(ModelFields.SessionResponse.previewAspectRatio)
    public float previewAspectRatio;
    @SerializedName(ModelFields.SessionResponse.layoutUpdatedAt)
    public int layoutUpdatedAt;
    @SerializedName(ModelFields.SessionResponse.isGameReaderDefaultFullscreen)
    public boolean isGameReaderDefaultFullscreen;
    @SerializedName(ModelFields.SessionResponse.isAllowProfiling)
    public boolean isAllowProfiling;
    @SerializedName(ModelFields.SessionResponse.isAllowStatV1)
    public Boolean isAllowStatV1;
    @SerializedName(ModelFields.SessionResponse.isAllowStatV2)
    public Boolean isAllowStatV2;
    @SerializedName(ModelFields.SessionResponse.isAllowCrash)
    public Boolean isAllowCrash;
    @SerializedName(ModelFields.SessionResponse.debugSessionKey)
    public String debugSessionKey;

    @SerializedName(ModelFields.SessionResponse.session)
    public Session session;

    @SerializedName(ModelFields.SessionResponse.sessionAssets)
    public List<SessionAsset> sessionAssets;

    @SerializedName(ModelFields.SessionResponse.placeholders)
    public List<StoryPlaceholder> placeholders;

    @SerializedName(ModelFields.SessionResponse.imagePlaceholders)
    public List<StoryPlaceholder> imagePlaceholders;

    public float getPreviewAspectRatio() {
        if (previewAspectRatio > 0) return previewAspectRatio;
        return 1f;
    }

}
