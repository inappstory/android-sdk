package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface CallToActionCallback {
    void callToAction(
            SlideData slide,
            String link,
            ClickAction action
    );
}
