package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.api.models.SessionAsset;

import java.util.List;

public interface IASContentPreload {
    void downloadSessionAssets(
            List<SessionAsset> sessionAssets
    );


}
