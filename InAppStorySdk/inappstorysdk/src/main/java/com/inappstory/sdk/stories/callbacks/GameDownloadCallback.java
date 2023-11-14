package com.inappstory.sdk.stories.callbacks;

import com.inappstory.sdk.core.models.api.GameCenterData;

public interface GameDownloadCallback {
    void complete(GameCenterData data);

    void error(String error);

}
