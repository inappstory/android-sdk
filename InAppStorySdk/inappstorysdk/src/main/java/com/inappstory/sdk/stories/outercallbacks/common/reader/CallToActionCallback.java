package com.inappstory.sdk.stories.outercallbacks.common.reader;

import android.content.Context;

import com.inappstory.sdk.stories.outercallbacks.common.objects.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

public interface CallToActionCallback {
    void callToAction(
            Context context,
            SlideData slide,
            String link,
            ClickAction action
    );
}
