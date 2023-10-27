package com.inappstory.sdk.core.repository.stories.interfaces;

import android.util.Pair;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.utils.IGetNetworkResponseCallback;

import java.util.List;

public interface IGetFeedCallback
        extends IGetNetworkResponseCallback<Pair<List<IPreviewStoryDTO>, Boolean>> {
}
