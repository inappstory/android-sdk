package com.inappstory.sdk.stories.outercallbacks.common.reader;

import android.content.Context;

public interface CallToActionCallback {
    void callToAction(
            Context context,
            SlideData slideData,
            String link,
            ClickAction action
    );
}
