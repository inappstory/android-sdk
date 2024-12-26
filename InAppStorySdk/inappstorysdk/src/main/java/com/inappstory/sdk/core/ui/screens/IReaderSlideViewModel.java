package com.inappstory.sdk.core.ui.screens;

import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.cache.ContentIdAndType;

public interface IReaderSlideViewModel {
    ContentIdAndType contentIdAndType();
    void contentLoadError();
    void slideLoadError(int index);
    void contentLoadSuccess(IReaderContent content);
    void slideLoadSuccess(int index);

    boolean loadContent();
}
