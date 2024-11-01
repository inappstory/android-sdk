package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentType;

public interface DownloadStoryCallback {
    void onDownload(IReaderContent story, int loadType, ContentType type);
    void onError(ContentIdAndType contentIdAndType);
}
