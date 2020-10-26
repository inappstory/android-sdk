package io.casestory.sdk.stories.api.models;


import java.util.List;

import io.casestory.sdk.network.SerializedName;

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
}
