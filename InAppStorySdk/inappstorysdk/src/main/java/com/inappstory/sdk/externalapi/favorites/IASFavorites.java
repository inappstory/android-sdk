package com.inappstory.sdk.externalapi.favorites;

import androidx.annotation.NonNull;

import com.inappstory.sdk.OldInAppStoryManager;
import com.inappstory.sdk.UseOldManagerInstanceCallback;

public class IASFavorites {
    public void removeAll() {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.removeAllFavorites();
            }
        });
    }

    public void removeByStoryId(final int storyId) {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.removeFromFavorite(storyId);
            }
        });
    }
}
