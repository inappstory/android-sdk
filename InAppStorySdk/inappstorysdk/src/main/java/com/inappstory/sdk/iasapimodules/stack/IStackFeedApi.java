package com.inappstory.sdk.iasapimodules.stack;


import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.List;

public interface IStackFeedApi {
    void getStackFeed(
            String feed,
            String uniqueStackId,
            List<String> tags,
            AppearanceManager appearanceManager,
            IStackFeedResult stackFeedResult
    );
}
