package com.inappstory.sdk.stories.api.models;

public class StatisticPermissions {
    public boolean allowProfiling = false;
    public boolean allowStatV1 = false;
    public boolean allowStatV2 = false;

    public StatisticPermissions() {
        allowProfiling = false;
        allowStatV1 = false;
        allowStatV2 = false;
    }


    public StatisticPermissions(
            boolean allowProfiling,
            boolean allowStatV1,
            boolean allowStatV2) {
        this.allowProfiling = allowProfiling;
        this.allowStatV1 = allowStatV1;
        this.allowStatV2 = allowStatV2;
    }
}
