package com.inappstory.sdk.refactoring.core.downloader;

import com.inappstory.sdk.refactoring.shared.data.contracts.ISlidesContent;
import com.inappstory.sdk.stories.cache.ContentIdAndType;

public interface IReaderContentDownloaderSubscriber {
    ContentIdAndType contentIdAndType();
    void contentLoadError();
    void slideLoadError(int index);
    void contentLoadSuccess(ISlidesContent content);
    void slideLoadSuccess(int index);
}
