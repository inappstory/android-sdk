package com.inappstory.sdk.stories.callbacks;

import com.inappstory.sdk.stories.api.models.Story;

public interface DownloadStoryCallback {
    void onDownload(Story story, int loadType);
}
