package com.inappstory.sdk.stories.api.models;


import java.util.List;

import com.inappstory.sdk.network.SerializedName;

/**
 * Created by paperrose on 19.02.2018.
 */

public class StatisticResponse {

    public StatisticSession session;
    @SerializedName("server_timestamp")
    public Long serverTimestamp;

   /* @SerializedName("share")
    public boolean share = false;*/

    @SerializedName("cache")
    public List<CacheFontObject> cachedFonts;

    @SerializedName("is_allow_profiling")
    public boolean isAllowProfiling;

    @SerializedName("placeholders")
    public List<StoryPlaceholder> placeholders;
}
