package com.inappstory.sdk.core.data;


import java.util.List;

public interface IInAppMessageLimitInCache {
    boolean isActive();

    List<IInAppMessageLimit> limits();
}
