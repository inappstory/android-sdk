package com.inappstory.sdk.core.repository.session.interfaces;

public interface IStatisticPermission {
    boolean isAllowStatV1();
    boolean isAllowStatV2();
    boolean isAllowProfiling();
    boolean isAllowCrash();
}
