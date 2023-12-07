package com.inappstory.sdk.stories.ui.reader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;

public interface IStoriesReaderContainer extends IStoriesReaderScreenChild {

    void forceClose();
    void close(CloseReader action, String cause);
}
