package com.inappstory.sdk.stories.outercallbacks.common.reader;

import android.content.Context;

import com.inappstory.sdk.core.api.IASCallback;

public interface CallToActionCallback extends IASCallback {
    void callToAction(
            Context context,
            ContentData slideData,
            String link,
            ClickAction action
    );
}
