package com.inappstory.sdk.stories.statistic;


import androidx.annotation.NonNull;

import com.inappstory.sdk.core.api.IASStatisticV1;

public interface GetStatisticV1Callback {
    void get(@NonNull IASStatisticV1 manager);
}
