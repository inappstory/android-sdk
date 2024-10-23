package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.core.dataholders.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;

public interface DownloadStoryCallback {
    void onDownload(IReaderContent story, int loadType, ContentType type);
    void onError(ViewContentTaskKey viewContentTaskKey);
}
