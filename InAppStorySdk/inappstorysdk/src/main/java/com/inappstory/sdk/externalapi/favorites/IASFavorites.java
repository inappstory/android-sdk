package com.inappstory.sdk.externalapi.favorites;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;

public class IASFavorites {
    public void removeAll() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.removeAllFavorites();
            }
        });
    }

    public void removeByStoryId(final int storyId) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.removeFromFavorite(storyId);
            }
        });
    }
}
