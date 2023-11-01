package com.inappstory.sdk.core.repository.stories.interfaces;

import com.inappstory.sdk.core.repository.stories.dto.IFavoritePreviewStoryDTO;
import com.inappstory.sdk.core.repository.utils.IGetNetworkResponseCallback;

import java.util.List;

public interface IGetFavoritePreviewsCallback
        extends IGetNetworkResponseCallback<List<IFavoritePreviewStoryDTO>> {
}
