package com.inappstory.sdk.externalapi.stackfeed;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.OldInAppStoryManager;
import com.inappstory.sdk.UseOldManagerInstanceCallback;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.List;

public class IASStackFeed {
    public void get(
            final String feed,
            final String uniqueStackId,
            final AppearanceManager appearanceManager,
            final List<String> tags,
            final IStackFeedResult stackFeedResult
    ) {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.getStackFeed(feed, uniqueStackId, tags, appearanceManager, stackFeedResult);
            }
        });
    }
}
