package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;

public interface IASUISettings {
    void setOpenStoriesReader(@NonNull IOpenStoriesReader openStoriesReader);

    void setOpenInAppMessageReader(@NonNull IOpenInAppMessageReader openInAppMessageReader);

    void setOpenGameReader(@NonNull IOpenGameReader openGameReader);
}
