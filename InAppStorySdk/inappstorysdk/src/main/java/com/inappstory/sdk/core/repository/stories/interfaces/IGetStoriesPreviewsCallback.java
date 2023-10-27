package com.inappstory.sdk.core.repository.stories.interfaces;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.utils.IGetNetworkResponseCallback;

import java.util.List;

public interface IGetStoriesPreviewsCallback extends IGetNetworkResponseCallback<List<IPreviewStoryDTO>> {
}
