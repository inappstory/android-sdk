package com.inappstory.sdk.core.repository.session.dto;

import com.inappstory.sdk.stories.api.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticPermissions;

public class StatisticPermissionDTO {
    public boolean isAllowProfiling() {
        return allowProfiling;
    }

    private void setAllowProfiling(Boolean allowProfiling) {
        if (allowProfiling != null)
            this.allowProfiling = allowProfiling;
    }

    public boolean isAllowStatV1() {
        return allowStatV1;
    }

    private void setAllowStatV1(Boolean allowStatV1) {
        if (allowStatV1 != null)
            this.allowStatV1 = allowStatV1;
    }

    public boolean isAllowStatV2() {
        return allowStatV2;
    }

    private void setAllowStatV2(Boolean allowStatV2) {
        if (allowStatV2 != null)
            this.allowStatV2 = allowStatV2;
    }

    public boolean isAllowCrash() {
        return allowCrash;
    }

    private void setAllowCrash(Boolean allowCrash) {
        if (allowCrash != null)
            this.allowCrash = allowCrash;
    }

    private boolean allowProfiling = false;
    private boolean allowStatV1 = true;
    private boolean allowStatV2 = false;
    private boolean allowCrash = false;

    public StatisticPermissionDTO(SessionResponse sessionResponse) {
        setAllowCrash(sessionResponse.isAllowCrash);
        setAllowProfiling(sessionResponse.isAllowProfiling);
        setAllowStatV1(sessionResponse.isAllowStatV1);
        setAllowStatV2(sessionResponse.isAllowStatV2);
    }
}
