package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface CallToActionCallback {
    void callToAction(int id,
                      String title,
                      String tags,
                      int slidesCount,
                      int index,
                      String link,
                      ClickAction action);
}
