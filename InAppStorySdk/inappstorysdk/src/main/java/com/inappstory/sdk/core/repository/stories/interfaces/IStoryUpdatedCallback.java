package com.inappstory.sdk.core.repository.stories.interfaces;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;

public interface IStoryUpdatedCallback {
    void onUpdate(IPreviewStoryDTO previewStoryDTO);
}
