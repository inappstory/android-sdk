package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface CustomActionCallback {
    void onAction(int id,
                  String title,
                  String tags,
                  int slidesCount,
                  int index,
                  String type,
                  String value);
}
