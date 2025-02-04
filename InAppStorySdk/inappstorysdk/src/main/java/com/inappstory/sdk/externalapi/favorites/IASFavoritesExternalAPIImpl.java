package com.inappstory.sdk.externalapi.favorites;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASFavorites;

public class IASFavoritesExternalAPIImpl implements IASFavorites {
    public void removeAll() {
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.favoritesAPI().removeAll();
            }
        });
    }

    public void removeByStoryId(final int storyId) {
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.favoritesAPI().removeByStoryId(storyId);
            }
        });
    }
}
