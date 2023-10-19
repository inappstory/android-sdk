package com.inappstory.sdk.stories.api.models;

public class StatisticPermissions {
    public boolean allowProfiling = false;
    public boolean allowStatV1 = true;
    public boolean allowStatV2 = false;
    public boolean allowCrash = false;

    public StatisticPermissions() {
        allowProfiling = false;
        allowStatV1 = true;
        allowStatV2 = false;
        allowCrash = false;
    }


    public StatisticPermissions(
            Boolean allowProfiling,
            Boolean allowStatV1,
            Boolean allowStatV2,
            Boolean allowCrash) {
        if (allowProfiling != null)
            this.allowProfiling = allowProfiling;
        if (allowStatV1 != null)
            this.allowStatV1 = allowStatV1;
        if (allowStatV2 != null)
            this.allowStatV2 = allowStatV2;
        if (allowCrash != null)
            this.allowCrash = allowCrash;
    }
}
