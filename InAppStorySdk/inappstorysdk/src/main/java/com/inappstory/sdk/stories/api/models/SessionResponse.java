package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;

/**
 * Created by paperrose on 19.02.2018.
 */

public class SessionResponse {

    public Session session;
    @SerializedName("server_timestamp")
    public Long serverTimestamp;

    @SerializedName("preview_aspect_ratio")
    public float previewAspectRatio;
    public SessionEditor editor;
    @SerializedName("cache")
    public List<CacheFontObject> cachedFonts;

    @SerializedName("is_allow_profiling")
    public boolean isAllowProfiling;
    @SerializedName("is_allow_statistic_v1")
    public Boolean isAllowStatV1;
    @SerializedName("is_allow_statistic_v2")
    public Boolean isAllowStatV2;
    @SerializedName("is_allow_crash")
    public Boolean isAllowCrash;

    public float getPreviewAspectRatio() {
        if (previewAspectRatio > 0) return previewAspectRatio;
        return 1f;
    }

    @SerializedName("placeholders")
    public List<StoryPlaceholder> placeholders;

    @SerializedName("image_placeholders")
    public List<StoryPlaceholder> imagePlaceholders;
}
