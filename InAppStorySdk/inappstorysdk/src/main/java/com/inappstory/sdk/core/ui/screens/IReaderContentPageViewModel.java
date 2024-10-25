package com.inappstory.sdk.core.ui.screens;

import com.inappstory.sdk.core.dataholders.IReaderContent;
import com.inappstory.sdk.stories.cache.ContentIdAndType;

public interface IReaderContentPageViewModel {
    ContentIdAndType contentIdAndType();
    void contentLoadError();
    void slideLoadError(int index);
    void contentLoadSuccess(IReaderContent content);
    void slideLoadSuccess(int index);
}
