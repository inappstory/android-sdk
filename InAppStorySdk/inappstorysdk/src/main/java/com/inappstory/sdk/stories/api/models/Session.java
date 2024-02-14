package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;

/**
 * Created by paperrose on 19.02.2018.
 */

public class Session {
    @Required
    public String id;
    @SerializedName("is_allow_profiling")
    public StatisticPermissions statisticPermissions;

    public boolean isAllowUgc;
}
