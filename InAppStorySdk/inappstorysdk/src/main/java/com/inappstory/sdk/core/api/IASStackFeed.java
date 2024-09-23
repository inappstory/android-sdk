package com.inappstory.sdk.core.api;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.List;

public interface IASStackFeed {
    void get(
            String feed,
            String uniqueStackId,
            AppearanceManager appearanceManager,
            List<String> tags,
            IStackFeedResult stackFeedResult
    );
}
