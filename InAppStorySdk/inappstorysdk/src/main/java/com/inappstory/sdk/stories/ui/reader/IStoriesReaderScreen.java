package com.inappstory.sdk.stories.ui.reader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;

public interface IStoriesReaderScreen {
    void forceClose();
    void close(CloseReader action, String cause);
}
