package com.inappstory.sdk.stories.callbacks;

import com.inappstory.sdk.stories.api.models.GameCenterData;

public interface GameDownloadCallback {
    void complete(GameCenterData data);

    void error();

}
