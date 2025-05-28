package com.inappstory.sdk.stories.ui;

import com.inappstory.sdk.core.ui.screens.BaseScreen;

public interface GetBaseReaderScreen<T extends BaseScreen> {
    void get(T readerScreen);
}