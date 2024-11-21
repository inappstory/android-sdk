package com.inappstory.sdk.stories.statistic;


import androidx.annotation.NonNull;

import com.inappstory.sdk.core.api.IASStatisticStoriesV1;

public interface GetStatisticV1Callback {
    void get(@NonNull IASStatisticStoriesV1 manager);
}
