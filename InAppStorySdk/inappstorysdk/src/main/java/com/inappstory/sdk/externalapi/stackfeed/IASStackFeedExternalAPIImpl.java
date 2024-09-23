package com.inappstory.sdk.externalapi.stackfeed;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.List;

public class IASStackFeedExternalAPIImpl implements IASStackFeed {
    public void get(
            final String feed,
            final String uniqueStackId,
            final AppearanceManager appearanceManager,
            final List<String> tags,
            final IStackFeedResult stackFeedResult
    ) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.getStackFeed(feed, uniqueStackId, tags, appearanceManager, stackFeedResult);
            }
        });
    }
}
