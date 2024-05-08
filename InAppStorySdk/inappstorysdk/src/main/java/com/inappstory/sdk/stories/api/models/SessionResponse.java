package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;

/**
 * Created by paperrose on 19.02.2018.
 */

public class SessionResponse {

    @SerializedName(SessionRequestFields.session)
    public Session session;

    @SerializedName(SessionRequestFields.isAllowUgc)
    public boolean isAllowUgc;
    @SerializedName(SessionRequestFields.serverTimestamp)
    public Long serverTimestamp;

    @SerializedName(SessionRequestFields.previewAspectRatio)
    public float previewAspectRatio;
    @SerializedName(SessionRequestFields.cacheObjects)
    public List<SessionCacheObject> cacheObjects;

    @SerializedName(SessionRequestFields.isAllowProfiling)
    public boolean isAllowProfiling;
    @SerializedName(SessionRequestFields.isAllowStatV1)
    public Boolean isAllowStatV1;
    @SerializedName(SessionRequestFields.isAllowStatV2)
    public Boolean isAllowStatV2;
    @SerializedName(SessionRequestFields.isAllowCrash)
    public Boolean isAllowCrash;

    public float getPreviewAspectRatio() {
        if (previewAspectRatio > 0) return previewAspectRatio;
        return 1f;
    }

    @SerializedName(SessionRequestFields.placeholders)
    public List<StoryPlaceholder> placeholders;

    @SerializedName(SessionRequestFields.imagePlaceholders)
    public List<StoryPlaceholder> imagePlaceholders;
}
