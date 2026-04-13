package com.inappstory.sdk.refactoring.core.downloader;

import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.cache.ContentIdAndType;

public interface IReaderContentDownloaderSubscriber {
    ContentIdAndType contentIdAndType();
    void contentLoadError();
    void slideLoadError(int index);
    void contentLoadSuccess(IReaderContent content);
    void slideLoadSuccess(int index);
}
