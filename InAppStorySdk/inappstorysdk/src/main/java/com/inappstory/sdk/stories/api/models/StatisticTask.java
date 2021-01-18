package com.inappstory.sdk.stories.api.models;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.network.SerializedName;
import com.inappstory.sdk.stories.statistic.PhoneAppData;
import com.inappstory.sdk.stories.utils.Sizes;

public class StatisticTask {
    public String event;
    public PhoneAppData app;
    public Long timestamp;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("session_id")
    public String sessionId;

    @SerializedName("story_id")
    public Integer storyId;
    @SerializedName("whence")
    public String whence;
    @SerializedName("cause")
    public String cause;
    @SerializedName("slide_index")
    public Integer slideIndex;
    @SerializedName("slide_total")
    public Integer slideTotal;
    @SerializedName("duration_ms")
    public Long durationMs;
    @SerializedName("spend_ms")
    public Long spendMs;
}
